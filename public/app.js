document.addEventListener("DOMContentLoaded", () => {
  const eventForm = document.getElementById("eventForm");
  const formMessage = document.getElementById("formMessage");
  const eventsContainer = document.getElementById("eventsContainer");
  const refreshBtn = document.getElementById("refreshBtn");
  const formTitle = document.getElementById("formTitle");
  const eventIdInput = document.getElementById("eventId");
  const submitBtn = document.getElementById("submitBtn");
  const cancelEditBtn = document.getElementById("cancelEditBtn");

  const API_BASE = "/api/events";

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
    }, 5000);
  };

  const createEventCard = (event) => {
    const card = document.createElement("div");
    card.className = "event-card";
    card.innerHTML = `
            <button class="delete-btn" data-id="${event.id}">
                <svg width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
            </button>
            <div class="event-detail">
                <span class="type-badge">${event.eventType}</span>
                <span class="amount-value">$${parseFloat(event.amount).toFixed(2)}</span>
            </div>
            <div class="event-detail">
                <span class="event-label">Event ID</span>
                <span class="event-value">#${event.id}</span>
            </div>
            <div class="event-detail">
                <span class="event-label">User ID</span>
                <span class="event-value">${event.userId}</span>
            </div>
            <div style="position:absolute; bottom: 1.5rem; right: 1.5rem;">
                <button class="edit-btn secondary-btn" style="padding: 0.3rem 0.6rem; border-radius: 4px; font-size: 0.8rem;" data-id="${event.id}">Edit</button>
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
            card.remove();
            showMessage("Event deleted successfully", false);
          } else {
            const err = await res.json();
            showMessage(err.message || "Failed to delete event", true);
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
      window.scrollTo({ top: 0, behavior: "smooth" });
    });

    return card;
  };

  const filterEvents = (events) => {
    const typeFilter = document
      .getElementById("filterEventType")
      .value.toLowerCase();
    const userFilter = document.getElementById("filterUserId").value;

    return events.filter((event) => {
      const matchType =
        typeFilter === "" || event.eventType.toLowerCase().includes(typeFilter);
      const matchUser =
        userFilter === "" || event.userId.toString() === userFilter;
      return matchType && matchUser;
    });
  };

  const fetchEvents = async () => {
    eventsContainer.innerHTML = '<div class="loading-spinner"></div>';
    try {
      const res = await fetch(API_BASE);
      if (!res.ok) {
        throw new Error("Failed to fetch events");
      }

      const events = await res.json();
      eventsContainer.innerHTML = "";

      const totalVolume = events.reduce(
        (sum, e) => sum + parseFloat(e.amount),
        0,
      );
      document.getElementById("totalEventsCount").textContent = events.length;
      document.getElementById("totalVolumeAmount").textContent =
        "$" + totalVolume.toFixed(2);

      const sortBy = document.getElementById("sortEvents").value;
      let processedEvents = filterEvents(events);

      processedEvents = processedEvents.sort((a, b) => {
        if (sortBy === "newest") return b.id - a.id;
        if (sortBy === "oldest") return a.id - b.id;
        if (sortBy === "amount-high")
          return parseFloat(b.amount) - parseFloat(a.amount);
        if (sortBy === "amount-low")
          return parseFloat(a.amount) - parseFloat(b.amount);
        return 0;
      });

      if (processedEvents.length === 0) {
        eventsContainer.innerHTML =
          '<p class="subtitle">No recent events found.</p>';
        return;
      }

      processedEvents.forEach((event) => {
        eventsContainer.appendChild(createEventCard(event));
      });
    } catch (error) {
      eventsContainer.innerHTML = `<p class="message error">Failed to load events: ${error.message}</p>`;
    }
  };

  eventForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
      userId: parseInt(document.getElementById("userId").value, 10),
      amount: parseFloat(document.getElementById("amount").value),
      eventType: document.getElementById("eventType").value,
    };
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
        fetchEvents();
      } else {
        const err = await res.json();
        showMessage(
          err.message ||
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

  refreshBtn.addEventListener("click", fetchEvents);
  document
    .getElementById("filterEventType")
    .addEventListener("input", fetchEvents);
  document
    .getElementById("filterUserId")
    .addEventListener("input", fetchEvents);
  document.getElementById("sortEvents").addEventListener("change", fetchEvents);

  fetchEvents();
});
