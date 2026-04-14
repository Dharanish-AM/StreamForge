document.addEventListener("DOMContentLoaded", () => {
  const eventForm = document.getElementById("eventForm");
  const eventsContainer = document.getElementById("eventsContainer");
  const refreshBtn = document.getElementById("refreshBtn");
  const formTitle = document.getElementById("formTitle");
  const eventIdInput = document.getElementById("eventId");
  const submitBtn = document.getElementById("submitBtn");
  const cancelEditBtn = document.getElementById("cancelEditBtn");
  const clearFormBtn = document.getElementById("clearFormBtn");
  const clearFiltersBtn = document.getElementById("clearFiltersBtn");
  const feedInfo = document.getElementById("feedInfo");
  const searchInput = document.getElementById("searchInput");
  const filterTypeInput = document.getElementById("filterEventType");
  const filterUserInput = document.getElementById("filterUserId");
  const sortEventsSelect = document.getElementById("sortEvents");
  const userIdInput = document.getElementById("userId");
  const amountInput = document.getElementById("amount");
  const eventTypeInput = document.getElementById("eventType");

  const API_BASE = "/api/events";
  const KPI_API = `${API_BASE}/kpi`;
  let allEvents = [];
  let isFetching = false;

  const debounce = (fn, wait = 220) => {
    let timeout;
    return (...args) => {
      clearTimeout(timeout);
      timeout = setTimeout(() => fn(...args), wait);
    };
  };

  const inrFormatter = new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });

  const toCurrency = (value) => inrFormatter.format(Number(value) || 0);

  const formatDate = (value) => {
    if (!value) {
      return "-";
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return "-";
    }

    return new Intl.DateTimeFormat(undefined, {
      year: "numeric",
      month: "short",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    }).format(date);
  };

  const getRelativeTime = (value) => {
    if (!value) {
      return "-";
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return "-";
    }

    const diffMs = date.getTime() - Date.now();
    const absMs = Math.abs(diffMs);
    const minutes = Math.round(absMs / 60000);
    const rtf = new Intl.RelativeTimeFormat(undefined, { numeric: "auto" });

    if (minutes < 60) {
      return rtf.format(Math.round(diffMs / 60000), "minute");
    }

    const hours = Math.round(absMs / 3600000);
    if (hours < 24) {
      return rtf.format(Math.round(diffMs / 3600000), "hour");
    }

    const days = Math.round(absMs / 86400000);
    return rtf.format(Math.round(diffMs / 86400000), "day");
  };

  const setRefreshingState = (loading) => {
    isFetching = loading;
    refreshBtn.disabled = loading;
    refreshBtn.textContent = loading ? "Refreshing..." : "Refresh";
  };

  const updateDashboard = (kpiStats = {}) => {
    document.getElementById("totalEventsCount").textContent = String(kpiStats.totalEvents ?? 0);
    document.getElementById("totalVolumeAmount").textContent = toCurrency(kpiStats.totalVolume ?? 0);
    document.getElementById("averageAmount").textContent = toCurrency(kpiStats.averageAmount ?? 0);
    document.getElementById("uniqueUsersCount").textContent = String(kpiStats.uniqueUsers ?? 0);
  };

  const updateFeedInfo = (visibleCount, totalCount) => {
    feedInfo.textContent = `Showing ${visibleCount} of ${totalCount} events`;
  };

  const extractErrorMessage = async (response) => {
    const contentType = response.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
      try {
        const body = await response.json();
        if (body && typeof body.message === "string") {
          return body.message;
        }
      } catch (error) {
        return "Request failed";
      }

      return "Request failed";
    }

    try {
      const text = await response.text();
      return text || "Request failed";
    } catch (error) {
      return "Request failed";
    }
  };

  const showMessage = (msg, isError) => {
    const toast = document.createElement("div");
    toast.className = `toast ${isError ? "error" : "success"}`;
    toast.innerHTML = `
      <div class="toast-icon">
        <svg width="24" height="24" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          ${
            isError
              ? '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>'
              : '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>'
          }
        </svg>
      </div>
      <div>${msg}</div>
    `;
    document.getElementById("toastContainer").appendChild(toast);

    setTimeout(() => toast.classList.add("show"), 10);

    setTimeout(() => {
      toast.classList.remove("show");
      setTimeout(() => toast.remove(), 300);
    }, 3600);
  };

  const createEventCard = (event) => {
    const card = document.createElement("div");
    card.className = "event-card";
    card.innerHTML = `
            <button class="delete-btn" data-id="${event.id}">
                <svg width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
            </button>
        <div class="event-top">
                <span class="type-badge">${event.eventType}</span>
          <span class="amount-value">${toCurrency(event.amount)}</span>
            </div>
            <div class="event-detail">
                <span class="event-label">Event ID</span>
                <span class="event-value">#${event.id}</span>
            </div>
            <div class="event-detail">
                <span class="event-label">User ID</span>
                <span class="event-value">${event.userId}</span>
            </div>
        <div class="event-detail">
          <span class="event-label">Created</span>
          <span class="event-value" title="${formatDate(event.createdAt)}">${getRelativeTime(event.createdAt)}</span>
        </div>
        <div class="event-actions">
          <button class="action-btn copy-btn" data-id="${event.id}">Copy ID</button>
          <button class="action-btn edit-btn" data-id="${event.id}">Edit</button>
            </div>
        `;

    card.querySelector(".delete-btn").addEventListener("click", async () => {
      const confirmed = window.confirm(
        "Are you sure you want to delete this event?",
      );
      if (confirmed) {
        try {
          const res = await fetch(`${API_BASE}/${event.id}`, {
            method: "DELETE",
          });
          if (res.ok) {
            await refreshData(false);
            showMessage("Event deleted successfully", false);
          } else {
            const message = await extractErrorMessage(res);
            showMessage(message || "Failed to delete event", true);
          }
        } catch (error) {
          showMessage("Network error occurred", true);
        }
      }
    });

    card.querySelector(".edit-btn").addEventListener("click", () => {
      eventIdInput.value = event.id;
      document.getElementById("userId").value = event.userId;
      document.getElementById("amount").value = event.amount;
      document.getElementById("eventType").value = event.eventType;
      formTitle.textContent = `Edit Event #${event.id}`;
      submitBtn.querySelector("span").textContent = "Update Event";
      cancelEditBtn.classList.remove("hidden");
      userIdInput.focus();
      window.scrollTo({ top: 0, behavior: "smooth" });
    });

    card.querySelector(".copy-btn").addEventListener("click", async () => {
      try {
        await navigator.clipboard.writeText(String(event.id));
        showMessage(`Copied Event ID #${event.id}`, false);
      } catch (error) {
        showMessage("Could not copy Event ID", true);
      }
    });

    return card;
  };

  const getFilteredAndSortedEvents = (events) => {
    const typeFilter = filterTypeInput.value.trim().toLowerCase();
    const userFilter = filterUserInput.value.trim();
    const searchText = searchInput.value.trim().toLowerCase();
    const sortBy = sortEventsSelect.value;

    let filtered = events.filter((event) => {
      const matchType =
        typeFilter === "" || event.eventType.toLowerCase().includes(typeFilter);
      const matchUser =
        userFilter === "" || event.userId.toString() === userFilter;

      const searchTarget = `${event.eventType} ${event.userId} ${event.id}`.toLowerCase();
      const matchSearch = searchText === "" || searchTarget.includes(searchText);

      return matchType && matchUser && matchSearch;
    });

    filtered = filtered.sort((a, b) => {
      if (sortBy === "newest") return b.id - a.id;
      if (sortBy === "oldest") return a.id - b.id;
      if (sortBy === "amount-high") return Number(b.amount) - Number(a.amount);
      if (sortBy === "amount-low") return Number(a.amount) - Number(b.amount);
      if (sortBy === "type-az") {
        return a.eventType.localeCompare(b.eventType, undefined, { sensitivity: "base" });
      }
      return 0;
    });

    return filtered;
  };

  const showLoading = () => {
    eventsContainer.innerHTML = '<div class="loading-spinner" aria-label="Loading events"></div>';
  };

  const renderEvents = () => {
    const processedEvents = getFilteredAndSortedEvents(allEvents);
    eventsContainer.innerHTML = "";
    updateFeedInfo(processedEvents.length, allEvents.length);

    if (processedEvents.length === 0) {
      const empty = document.createElement("div");
      empty.className = "empty-state";
      empty.textContent =
        allEvents.length === 0
          ? "No events exist yet. Create one using the form above."
          : "No events match your current filters.";
      eventsContainer.appendChild(empty);
      return;
    }

    const fragment = document.createDocumentFragment();
    processedEvents.forEach((event) => {
      fragment.appendChild(createEventCard(event));
    });
    eventsContainer.appendChild(fragment);
  };

  const fetchEvents = async () => {
    try {
      const res = await fetch(API_BASE);
      if (!res.ok) {
        throw new Error(await extractErrorMessage(res));
      }

      allEvents = await res.json();
      renderEvents();
      return true;
    } catch (error) {
      eventsContainer.innerHTML = `<p class="empty-state">Failed to load events: ${error.message}</p>`;
      updateFeedInfo(0, 0);
      return false;
    }
  };

  const fetchKpiStats = async () => {
    try {
      const res = await fetch(KPI_API);
      if (!res.ok) {
        throw new Error(await extractErrorMessage(res));
      }

      const kpiStats = await res.json();
      updateDashboard(kpiStats);
      return true;
    } catch (error) {
      updateDashboard();
      return false;
    }
  };

  const refreshData = async (showSpinner = true) => {
    if (!isFetching && showSpinner) {
      showLoading();
    }

    setRefreshingState(true);

    const [eventsResult, kpiResult] = await Promise.all([
      fetchEvents(),
      fetchKpiStats(),
    ]);

    if (!eventsResult) {
      showMessage("Failed to load events", true);
    }

    if (!kpiResult) {
      showMessage("Failed to load KPI stats", true);
    }

    setRefreshingState(false);
  };

  const validatePayload = (payload) => {
    if (!Number.isInteger(payload.userId) || payload.userId <= 0) {
      return "User ID must be a positive integer.";
    }

    if (Number.isNaN(payload.amount) || payload.amount <= 0) {
      return "Amount (INR) must be greater than 0.";
    }

    if (!payload.eventType || payload.eventType.trim().length < 2) {
      return "Event Type must contain at least 2 characters.";
    }

    return "";
  };

  eventForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
      userId: Number.parseInt(userIdInput.value, 10),
      amount: Number.parseFloat(amountInput.value),
      eventType: eventTypeInput.value.trim(),
    };

    const validationError = validatePayload(payload);
    if (validationError) {
      showMessage(validationError, true);
      return;
    }

    const originalContent = submitBtn.innerHTML;
    submitBtn.innerHTML = "<span>Submitting...</span>";
    submitBtn.disabled = true;

    try {
      const eventId = eventIdInput.value;
      const method = eventId ? "PUT" : "POST";
      const url = eventId ? `${API_BASE}/${eventId}` : API_BASE;

      const res = await fetch(url, {
        method: method,
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      if (res.ok) {
        showMessage(
          eventId
            ? "Event updated successfully!"
            : "Event created successfully!",
          false,
        );
        resetForm();
        await refreshData(false);
      } else {
        const message = await extractErrorMessage(res);
        showMessage(
          message ||
            (eventId ? "Failed to update event" : "Failed to create event"),
          true,
        );
      }
    } catch (error) {
      showMessage("Network error occurred", true);
    } finally {
      submitBtn.innerHTML = originalContent;
      submitBtn.disabled = false;
    }
  });

  const resetForm = () => {
    eventForm.reset();
    eventIdInput.value = "";
    formTitle.textContent = "Create New Event";
    submitBtn.querySelector("span").textContent = "Submit Event";
    cancelEditBtn.classList.add("hidden");
  };

  cancelEditBtn.addEventListener("click", resetForm);
  clearFormBtn.addEventListener("click", resetForm);

  const clearFilters = () => {
    searchInput.value = "";
    filterTypeInput.value = "";
    filterUserInput.value = "";
    sortEventsSelect.value = "newest";
    renderEvents();
  };

  clearFiltersBtn.addEventListener("click", clearFilters);

  refreshBtn.addEventListener("click", () => refreshData(true));

  const renderEventsDebounced = debounce(renderEvents, 220);
  filterTypeInput.addEventListener("input", renderEventsDebounced);
  filterUserInput.addEventListener("input", renderEventsDebounced);
  searchInput.addEventListener("input", renderEventsDebounced);
  sortEventsSelect.addEventListener("change", renderEvents);

  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape" && !cancelEditBtn.classList.contains("hidden")) {
      resetForm();
      showMessage("Edit cancelled", false);
    }
  });

  refreshData(true);
});
