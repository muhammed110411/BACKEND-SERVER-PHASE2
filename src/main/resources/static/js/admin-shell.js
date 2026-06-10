(function () {
    var STORAGE_KEY = "phase2.admin.settings";
    var DEFAULTS = {
        theme: "dark",
        language: "en",
        tableDensity: "balanced",
        chartDensity: "balanced",
        driversDefaultView: "list",
        sidebarCollapsed: false
    };

    var I18N = {
        en: {
            "nav.operations": "Operations",
            "nav.dashboard": "Dashboard",
            "nav.drivers": "Drivers",
            "nav.sessions": "Sessions",
            "nav.analytics": "Fleet Analytics",
            "nav.vehicles": "Vehicles",
            "nav.settings": "Settings",
            "settings.kicker": "Interface preferences",
            "settings.title": "Settings",
            "settings.localNote": "These preferences are stored locally in your browser and never affect business data or backend logic.",
            "settings.unsaved": "You have unsaved changes",
            "settings.saved": "Settings saved successfully",
            "settings.failed": "Failed to save settings. Please try again.",
            "settings.appearance": "Appearance",
            "settings.appearanceHelp": "Control the visual theme and interface language.",
            "settings.theme": "Theme",
            "settings.themeHelp": "Choose between dark, light, or system-matched appearance.",
            "settings.dark": "Dark",
            "settings.light": "Light",
            "settings.system": "System",
            "settings.language": "Language",
            "settings.languageHelp": "Choose the admin interface language.",
            "settings.display": "Display",
            "settings.displayHelp": "Adjust spacing in data-heavy views.",
            "settings.tableDensity": "Table Density",
            "settings.tableDensityHelp": "Controls row height and padding in tables.",
            "settings.chartDensity": "Chart Density",
            "settings.chartDensityHelp": "Adjusts spacing inside chart panels.",
            "settings.compact": "Compact",
            "settings.balanced": "Balanced",
            "settings.comfortable": "Comfortable",
            "settings.spacious": "Spacious",
            "settings.browsing": "Browsing Preferences",
            "settings.browsingHelp": "Set default behavior for admin navigation.",
            "settings.defaultDriversView": "Default Drivers View",
            "settings.defaultDriversViewHelp": "Determines which mode the Drivers page opens in. You can still switch modes from within the Drivers page.",
            "settings.listView": "List View",
            "settings.cardGrid": "Card Grid",
            "settings.compactSidebar": "Start with Compact Sidebar",
            "settings.compactSidebarHelp": "When enabled, the sidebar opens in a narrower icon-only state on page load.",
            "settings.reset": "Reset to Defaults",
            "settings.save": "Save Changes"
        },
        tr: {
            "nav.operations": "İşlemler",
            "nav.dashboard": "Gösterge Paneli",
            "nav.drivers": "Sürücüler",
            "nav.sessions": "Oturumlar",
            "nav.analytics": "Filo Analitiği",
            "nav.vehicles": "Araçlar",
            "nav.settings": "Ayarlar",
            "settings.kicker": "Arayüz tercihleri",
            "settings.title": "Ayarlar",
            "settings.localNote": "Bu tercihler tarayıcınızda yerel olarak saklanır ve iş verilerini ya da backend mantığını etkilemez.",
            "settings.unsaved": "Kaydedilmemiş değişiklikleriniz var",
            "settings.saved": "Ayarlar başarıyla kaydedildi",
            "settings.failed": "Ayarlar kaydedilemedi. Lütfen tekrar deneyin.",
            "settings.appearance": "Görünüm",
            "settings.appearanceHelp": "Görsel temayı ve arayüz dilini ayarlayın.",
            "settings.theme": "Tema",
            "settings.themeHelp": "Koyu, açık veya sistem görünümünü seçin.",
            "settings.dark": "Koyu",
            "settings.light": "Açık",
            "settings.system": "Sistem",
            "settings.language": "Dil",
            "settings.languageHelp": "Yönetim arayüzü dilini seçin.",
            "settings.display": "Görüntü",
            "settings.displayHelp": "Veri yoğun görünümlerde aralığı ayarlayın.",
            "settings.tableDensity": "Tablo Yoğunluğu",
            "settings.tableDensityHelp": "Tablolarda satır yüksekliğini ve dolguyu ayarlar.",
            "settings.chartDensity": "Grafik Yoğunluğu",
            "settings.chartDensityHelp": "Grafik panellerindeki aralığı ayarlar.",
            "settings.compact": "Kompakt",
            "settings.balanced": "Dengeli",
            "settings.comfortable": "Rahat",
            "settings.spacious": "Geniş",
            "settings.browsing": "Gezinme Tercihleri",
            "settings.browsingHelp": "Yönetim gezinmesi için varsayılan davranışı ayarlayın.",
            "settings.defaultDriversView": "Varsayılan Sürücüler Görünümü",
            "settings.defaultDriversViewHelp": "Sürücüler sayfasının hangi modda açılacağını belirler. Sürücüler sayfasında modu yine değiştirebilirsiniz.",
            "settings.listView": "Liste Görünümü",
            "settings.cardGrid": "Kart Izgarası",
            "settings.compactSidebar": "Kompakt Kenar Çubuğu ile Başla",
            "settings.compactSidebarHelp": "Etkinleştirildiğinde kenar çubuğu sayfa yüklenirken daha dar, yalnızca ikonlu durumda açılır.",
            "settings.reset": "Varsayılanlara Sıfırla",
            "settings.save": "Değişiklikleri Kaydet"
        }
    };

    function readRaw() {
        try {
            return JSON.parse(window.localStorage.getItem(STORAGE_KEY) || "{}");
        } catch (error) {
            return {};
        }
    }

    function normalize(input) {
        var output = {};
        Object.keys(DEFAULTS).forEach(function (key) {
            output[key] = Object.prototype.hasOwnProperty.call(input || {}, key) ? input[key] : DEFAULTS[key];
        });
        return output;
    }

    function writeRaw(values) {
        try {
            window.localStorage.setItem(STORAGE_KEY, JSON.stringify(normalize(values)));
            return true;
        } catch (error) {
            return false;
        }
    }

    function resolvedTheme(choice) {
        if (choice === "system" && window.matchMedia) {
            return window.matchMedia("(prefers-color-scheme: light)").matches ? "light" : "dark";
        }
        return choice === "light" ? "light" : "dark";
    }

    function translate(values) {
        var language = values.language === "tr" ? "tr" : "en";
        var dictionary = I18N[language];
        document.documentElement.lang = language;

        Array.prototype.slice.call(document.querySelectorAll("[data-i18n]")).forEach(function (element) {
            var key = element.getAttribute("data-i18n");
            if (dictionary[key]) {
                element.textContent = dictionary[key];
            }
        });

        Array.prototype.slice.call(document.querySelectorAll("[data-nav-key]")).forEach(function (item) {
            var key = item.getAttribute("data-nav-key");
            if (dictionary[key]) {
                item.setAttribute("title", dictionary[key]);
                item.setAttribute("aria-label", dictionary[key]);
            }
        });

        var activeNav = document.querySelector(".nav-item[aria-current='page'], .nav-item.is-active");
        var routeTitle = document.querySelector(".route-title");
        if (activeNav && routeTitle) {
            var navKey = activeNav.getAttribute("data-nav-key");
            if (dictionary[navKey]) {
                routeTitle.textContent = dictionary[navKey];
            }
        }
    }

    function apply(values) {
        var settings = normalize(values || readRaw());
        var theme = resolvedTheme(settings.theme);
        var root = document.documentElement;
        var shell = document.querySelector("[data-admin-shell]");

        root.setAttribute("data-admin-theme-choice", settings.theme);
        root.setAttribute("data-admin-theme", theme);
        root.setAttribute("data-table-density", settings.tableDensity);
        root.setAttribute("data-chart-density", settings.chartDensity);

        if (shell) {
            shell.classList.toggle("is-compact-sidebar", Boolean(settings.sidebarCollapsed));
        }

        translate(settings);
        return settings;
    }

    window.Phase2AdminSettings = {
        defaults: DEFAULTS,
        load: function () { return normalize(readRaw()); },
        save: writeRaw,
        apply: apply
    };

    apply();

    if (window.matchMedia) {
        var themeQuery = window.matchMedia("(prefers-color-scheme: light)");
        var onSystemThemeChange = function () {
            if (window.Phase2AdminSettings.load().theme === "system") {
                apply();
            }
        };
        if (themeQuery.addEventListener) {
            themeQuery.addEventListener("change", onSystemThemeChange);
        } else if (themeQuery.addListener) {
            themeQuery.addListener(onSystemThemeChange);
        }
    }
}());

(function () {
    var shell = document.querySelector("[data-admin-shell]");

    if (!shell) {
        return;
    }

    var menuButton = shell.querySelector("[data-sidebar-toggle]");
    var backdrop = shell.querySelector("[data-sidebar-backdrop]");
    var profileButton = shell.querySelector("[data-profile-trigger]");
    var profileMenu = shell.querySelector("[data-profile-menu]");

    function setSidebarOpen(isOpen) {
        shell.classList.toggle("is-sidebar-open", isOpen);
        if (menuButton) {
            menuButton.setAttribute("aria-expanded", isOpen ? "true" : "false");
        }
    }

    function setProfileOpen(isOpen) {
        if (!profileButton || !profileMenu) {
            return;
        }

        profileMenu.hidden = !isOpen;
        profileButton.setAttribute("aria-expanded", isOpen ? "true" : "false");
    }

    if (menuButton) {
        menuButton.addEventListener("click", function () {
            setSidebarOpen(!shell.classList.contains("is-sidebar-open"));
        });
    }

    if (backdrop) {
        backdrop.addEventListener("click", function () {
            setSidebarOpen(false);
        });
    }

    if (profileButton && profileMenu) {
        profileButton.addEventListener("click", function (event) {
            event.stopPropagation();
            setProfileOpen(profileMenu.hidden);
        });

        document.addEventListener("click", function (event) {
            if (!profileMenu.hidden && !profileMenu.contains(event.target) && event.target !== profileButton) {
                setProfileOpen(false);
            }
        });
    }

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            setSidebarOpen(false);
            setProfileOpen(false);
        }
    });
}());

(function () {
    var page = document.querySelector("[data-drivers-page]");

    if (!page) {
        return;
    }

    var storageKey = "phase2.admin.drivers";
    var searchInput = page.querySelector("[data-driver-search]");
    var sortSelect = page.querySelector("[data-driver-sort]");
    var sortByInput = page.querySelector("[data-driver-sort-by]");
    var sortDirectionInput = page.querySelector("[data-driver-sort-direction]");
    var controlsForm = page.querySelector("[data-driver-controls]");
    var listMode = page.querySelector("[data-driver-list-mode]");
    var gridMode = page.querySelector("[data-driver-grid-mode]");
    var grid = page.querySelector("[data-driver-grid]");
    var noResults = page.querySelector("[data-no-results]");
    var visibleCount = page.querySelector("[data-visible-count]");
    var modeButtons = Array.prototype.slice.call(page.querySelectorAll("[data-driver-mode]"));
    var manualOrder = [];
    var activeMode = "list";
    var focusedCard = null;
    var activeInlinePanel = null;
    var activeCardPanel = null;
    var cardBackdrop = page.querySelector("[data-driver-card-backdrop]");
    var cardOverlay = page.querySelector("[data-driver-card-overlay]");
    var panelRequestSequence = 0;

    function loadState() {
        try {
            return JSON.parse(window.localStorage.getItem(storageKey) || "{}");
        } catch (error) {
            return {};
        }
    }

    function saveState(extra) {
        var state = loadState();
        Object.keys(extra).forEach(function (key) {
            state[key] = extra[key];
        });
        window.localStorage.setItem(storageKey, JSON.stringify(state));
    }

    function getItems(scope) {
        return Array.prototype.slice.call((scope || page).querySelectorAll("[data-driver-item]"));
    }

    function getInlineRow(item) {
        return item && item.nextElementSibling && item.nextElementSibling.matches("[data-driver-inline-row]")
                ? item.nextElementSibling
                : null;
    }

    function itemMatches(item) {
        var query = (searchInput ? searchInput.value : "").trim().toLowerCase();
        var haystack = [
            item.getAttribute("data-driver-name"),
            item.getAttribute("data-driver-id"),
            item.getAttribute("data-driver-email")
        ].join(" ").toLowerCase();

        return !query || haystack.indexOf(query) !== -1;
    }

    function compareItems(sortValue) {
        if (sortValue === "manual") {
            sortValue = "name:asc";
        }
        var parts = (sortValue || "name:asc").split(":");
        var field = parts[0];
        var direction = parts[1] === "asc" ? 1 : -1;

        return function (left, right) {
            var leftValue;
            var rightValue;

            if (field === "name") {
                leftValue = (left.getAttribute("data-driver-name") || "").toLowerCase();
                rightValue = (right.getAttribute("data-driver-name") || "").toLowerCase();
                return leftValue.localeCompare(rightValue) * direction;
            }

            if (field === "longTermReliabilityScore") {
                leftValue = parseFloat(left.getAttribute("data-driver-score") || "0");
                rightValue = parseFloat(right.getAttribute("data-driver-score") || "0");
            } else if (field === "totalSessions") {
                leftValue = parseInt(left.getAttribute("data-driver-sessions") || "0", 10);
                rightValue = parseInt(right.getAttribute("data-driver-sessions") || "0", 10);
            } else if (field === "totalDistanceKm") {
                leftValue = parseFloat(left.getAttribute("data-driver-distance") || "0");
                rightValue = parseFloat(right.getAttribute("data-driver-distance") || "0");
            } else {
                leftValue = (left.getAttribute("data-driver-name") || "").toLowerCase();
                rightValue = (right.getAttribute("data-driver-name") || "").toLowerCase();
                return leftValue.localeCompare(rightValue) * direction;
            }

            return (leftValue - rightValue) * direction;
        };
    }

    function sortContainer(container) {
        if (!container || !sortSelect) {
            return;
        }

        var items = getItems(container);
        var pairedRows = new Map();
        items.forEach(function (item) {
            var row = getInlineRow(item);
            if (row) {
                pairedRows.set(item, row);
            }
        });
        if (activeMode === "grid" && manualOrder.length > 0 && sortSelect.value === "manual") {
            items.sort(function (left, right) {
                var leftIndex = manualOrder.indexOf(left.getAttribute("data-driver-id"));
                var rightIndex = manualOrder.indexOf(right.getAttribute("data-driver-id"));
                return (leftIndex === -1 ? 9999 : leftIndex) - (rightIndex === -1 ? 9999 : rightIndex);
            });
        } else {
            items.sort(compareItems(sortSelect.value));
        }

        items.forEach(function (item) {
            container.appendChild(item);
            if (pairedRows.has(item)) {
                container.appendChild(pairedRows.get(item));
            }
        });
    }

    function syncSortInputs() {
        if (!sortSelect || !sortByInput || !sortDirectionInput) {
            return;
        }
        var parts = (sortSelect.value === "manual" ? "name:asc" : sortSelect.value).split(":");
        sortByInput.value = parts[0] || "name";
        sortDirectionInput.value = parts[1] || "asc";
    }

    function updateCounts() {
        var visibleItems = getItems(grid || page).filter(function (item) {
            return !item.hidden;
        });
        if (visibleCount) {
            visibleCount.textContent = String(visibleItems.length);
        }

        if (noResults) {
            noResults.hidden = visibleItems.length !== 0;
        }
    }

    function applyFiltersAndSort() {
        var containers = [page.querySelector("tbody"), grid].filter(Boolean);

        containers.forEach(function (container) {
            sortContainer(container);
            getItems(container).forEach(function (item) {
                var hidden = !itemMatches(item);
                var inlineRow = getInlineRow(item);
                item.hidden = hidden;
                if (hidden && activeInlinePanel && activeInlinePanel.item === item) {
                    collapseInlinePanel(true);
                }
                if (hidden && activeCardPanel && activeCardPanel.item === item) {
                    collapseCardOverlay(true);
                }
                if (hidden && inlineRow) {
                    inlineRow.hidden = true;
                }
            });
        });

        syncSortInputs();
        updateCounts();
        saveState({
            search: searchInput ? searchInput.value : "",
            sort: sortSelect ? sortSelect.value : "name:asc"
        });
    }

    function setMode(mode) {
        var nextMode = mode === "grid" ? "grid" : "list";
        if (nextMode !== activeMode) {
            collapseInlinePanel(true);
            collapseCardOverlay(true);
        }
        activeMode = nextMode;
        if (listMode) {
            listMode.hidden = activeMode !== "list";
        }
        if (gridMode) {
            gridMode.hidden = activeMode !== "grid";
        }
        modeButtons.forEach(function (button) {
            var isActive = button.getAttribute("data-driver-mode") === activeMode;
            button.classList.toggle("is-active", isActive);
            button.setAttribute("aria-pressed", isActive ? "true" : "false");
        });
        saveState({ mode: activeMode });
        applyFiltersAndSort();
    }

    function rememberManualOrder() {
        if (!grid) {
            return;
        }
        if (sortSelect) {
            sortSelect.value = "manual";
        }
        manualOrder = getItems(grid).map(function (item) {
            return item.getAttribute("data-driver-id");
        });
        syncSortInputs();
        saveState({ manualOrder: manualOrder, sort: "manual" });
    }

    function swapCards(source, target) {
        if (!grid || !source || !target || source === target) {
            return;
        }

        var sourceMarker = document.createComment("driver-source");
        var targetMarker = document.createComment("driver-target");
        grid.insertBefore(sourceMarker, source);
        grid.insertBefore(targetMarker, target);
        grid.insertBefore(source, targetMarker);
        grid.insertBefore(target, sourceMarker);
        grid.removeChild(sourceMarker);
        grid.removeChild(targetMarker);
        rememberManualOrder();
    }

    function clearFocusedCard() {
        if (focusedCard) {
            focusedCard.classList.remove("is-focused", "is-expanded");
        }
        focusedCard = null;
        if (grid) {
            grid.classList.remove("has-focused-card");
        }
    }

    function focusCard(card) {
        if (!grid || !card) {
            return;
        }
        if (focusedCard && focusedCard !== card) {
            focusedCard.classList.remove("is-focused", "is-expanded");
        }
        focusedCard = card;
        grid.classList.add("has-focused-card");
        card.classList.add("is-focused");
    }

    function formatNumber(value, digits) {
        var number = Number(value || 0);
        return Number.isFinite(number) ? number.toFixed(digits) : "0.0";
    }

    function formatDate(value) {
        var timestamp = Number(value || 0);
        if (!Number.isFinite(timestamp) || timestamp <= 0) {
            return "";
        }
        return new Intl.DateTimeFormat(undefined, {
            year: "numeric",
            month: "short",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit"
        }).format(new Date(timestamp));
    }

    function trendDate(value, includeTime) {
        var timestamp = Number(value || 0);
        if (!Number.isFinite(timestamp) || timestamp <= 0) {
            return "Not available";
        }
        var normalized = timestamp > 1000000000000 ? timestamp : timestamp * 1000;
        var options = includeTime
                ? { year: "numeric", month: "short", day: "2-digit", hour: "2-digit", minute: "2-digit" }
                : { month: "short", day: "2-digit" };
        return new Intl.DateTimeFormat(undefined, options).format(new Date(normalized));
    }

    function formatDuration(secondsValue) {
        var seconds = Number(secondsValue || 0);
        if (!Number.isFinite(seconds) || seconds <= 0) {
            return "0s";
        }
        var hours = Math.floor(seconds / 3600);
        var minutes = Math.floor((seconds % 3600) / 60);
        var remaining = Math.floor(seconds % 60);

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        if (minutes > 0) {
            return minutes + "m " + remaining + "s";
        }
        return remaining + "s";
    }

    function pluralize(value, singular, plural) {
        return Number(value || 0) === 1 ? singular : plural;
    }

    function escapeHtml(value) {
        return String(value == null ? "" : value)
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#39;");
    }

    function panelTitle(panelType) {
        return panelType === "analytics" ? "Driver Analytics" : "Driver Details";
    }

    function panelKicker(panelType) {
        return panelType === "analytics" ? "Driver-specific analytics" : "Driver profile";
    }

    function panelUrl(item, panelType) {
        var driverId = item.getAttribute("data-driver-id");
        return panelType === "analytics"
                ? "/api/admin/drivers/" + encodeURIComponent(driverId) + "/analytics"
                : "/api/admin/drivers/" + encodeURIComponent(driverId);
    }

    function humanizeLabel(value) {
        return String(value || "Unknown")
                .replace(/_/g, " ")
                .toLowerCase()
                .replace(/\b[a-z]/g, function (letter) {
                    return letter.toUpperCase();
                });
    }

    function formatPercent(value, total) {
        var number = Number(value || 0);
        var base = Number(total || 0);
        return base > 0 ? formatNumber((number / base) * 100, 0) + "%" : "0%";
    }

    function detailRow(label, value, isCode) {
        var displayValue = value == null || value === "" ? "Not available" : value;
        return '<div><dt>' + escapeHtml(label) + '</dt><dd>'
                + (isCode ? '<code>' + escapeHtml(displayValue) + '</code>' : escapeHtml(displayValue))
                + '</dd></div>';
    }

    function summaryMetric(label, value, tone) {
        return '<article class="driver-summary-metric' + (tone ? " " + tone : "") + '">'
                + '<span>' + escapeHtml(label) + '</span>'
                + '<strong>' + escapeHtml(value) + '</strong>'
                + '</article>';
    }

    function driverPanelContext(item, data) {
        data = data || {};
        return {
            driverId: data.driverId || (item ? item.getAttribute("data-driver-id") : "") || "",
            driverName: (item ? item.getAttribute("data-driver-name") : "") || "Driver profile"
        };
    }

    function renderSubviewTitle(panelType, driverId) {
        return '<div class="driver-subview-title">'
                + '<span>' + escapeHtml(panelKicker(panelType)) + '</span>'
                + '<h3>' + escapeHtml(panelTitle(panelType)) + '</h3>'
                + (driverId ? '<p>' + escapeHtml(driverId) + '</p>' : "")
                + '</div>';
    }

    function renderPanelShell(panelType, bodyHtml) {
        return '<div class="driver-panel-head">'
                + '<div><span>' + escapeHtml(panelKicker(panelType)) + '</span><strong>' + escapeHtml(panelTitle(panelType)) + '</strong></div>'
                + '<button type="button" data-driver-panel-close aria-label="Close">X</button>'
                + '</div>'
                + bodyHtml;
    }

    function renderPanelLoading(panelType, includeTitle) {
        return '<section class="driver-subview-section">'
                + (includeTitle ? renderSubviewTitle(panelType, "") : "")
                + '<p class="driver-panel-empty">Loading...</p>'
                + '</section>';
    }

    function renderPanelError(panelType, includeTitle) {
        return '<section class="driver-subview-section">'
                + (includeTitle ? renderSubviewTitle(panelType, "") : "")
                + '<p class="driver-panel-empty">This data could not be loaded.</p>'
                + '</section>';
    }

    function renderDetails(data, includeTitle) {
        data = data || {};
        var created = formatDate(data.createdAt);
        var updated = formatDate(data.updatedAt);
        var sessionPage = data.sessionPage || {};
        var sessions = Array.isArray(sessionPage.items) ? sessionPage.items : [];
        return '<section class="driver-subview-section driver-details-subview">'
                + (includeTitle ? renderSubviewTitle("details", data.driverId) : "")
                + '<article class="driver-profile-section">'
                + '<header class="driver-section-head"><div><span>Driver Profile</span><h4>Profile</h4></div></header>'
                + '<dl class="driver-detail-list">'
                + detailRow("Driver ID", data.driverId || "", true)
                + detailRow("Name", data.driverName || "", false)
                + detailRow("Email", data.email || "", true)
                + detailRow("Role", data.role || "DRIVER", false)
                + detailRow("Account status", humanizeLabel(data.accountStatus), false)
                + detailRow("Reliability", formatNumber(data.longTermReliabilityScore, 1), false)
                + detailRow("Total sessions", data.totalSessions || 0, false)
                + detailRow("Total distance", formatNumber(data.totalDistanceKm, 1) + " km", false)
                + detailRow("Created", created, false)
                + detailRow("Updated", updated, false)
                + '</dl>'
                + '</article>'
                + renderDriverSessions(sessions, sessionPage, data.sessionLoadError)
                + '</section>';
    }

    function sessionToneClass(value) {
        var normalized = String(value || "").toUpperCase();
        if (normalized === "VALID" || normalized === "COMPLETED") {
            return " success";
        }
        if (normalized === "INVALID" || normalized === "ABORTED" || normalized === "FAILED") {
            return " danger";
        }
        return "";
    }

    function renderDriverSessionRow(session) {
        var sessionId = session.sessionId || "";
        var href = "/admin/sessions/" + encodeURIComponent(sessionId)
                + "?returnTo=" + encodeURIComponent(window.location.pathname + window.location.search);
        var dateLabel = formatDate(session.endTimestamp || session.uploadedAt || session.startTimestamp);
        return '<tr>'
                + '<td><a class="entity-link" href="' + href + '">' + escapeHtml(sessionId || "Unavailable") + '</a></td>'
                + '<td><code>' + escapeHtml(session.vehicleId || "Unavailable") + '</code></td>'
                + '<td>' + escapeHtml(dateLabel || "Not available") + '</td>'
                + '<td><strong>' + escapeHtml(formatNumber(session.finalScore, 1)) + '</strong></td>'
                + '<td><span class="status-pill' + sessionToneClass(session.validity) + '">' + escapeHtml(humanizeLabel(session.validity)) + '</span></td>'
                + '<td><span class="status-pill' + sessionToneClass(session.status) + '">' + escapeHtml(humanizeLabel(session.status)) + '</span></td>'
                + '<td>' + escapeHtml(formatDuration(session.totalDurationSeconds)) + '</td>'
                + '<td>' + escapeHtml(formatNumber(session.totalDistanceKm, 1)) + ' km</td>'
                + '<td>' + escapeHtml(session.totalEventCount || 0) + '</td>'
                + '<td>' + escapeHtml(session.totalEscalationCount || 0) + '</td>'
                + '</tr>';
    }

    function renderDriverSessions(sessions, sessionPage, failed) {
        var total = sessionPage && Number(sessionPage.totalItems || 0);
        if (failed) {
            return '<article class="driver-sessions-section">'
                    + '<header class="driver-section-head"><div><span>Driver Sessions</span><h4>Session history</h4></div></header>'
                    + '<p class="driver-panel-empty">Driver session history is not available from the current view model.</p>'
                    + '</article>';
        }

        return '<article class="driver-sessions-section">'
                + '<header class="driver-section-head"><div><span>Driver Sessions</span><h4>Session history</h4></div>'
                + '<strong>' + escapeHtml(Number.isFinite(total) ? total : sessions.length) + ' records</strong></header>'
                + (sessions.length
                        ? '<div class="driver-session-table-wrap"><table class="driver-session-table">'
                                + '<thead><tr><th scope="col">Session</th><th scope="col">Vehicle</th><th scope="col">Date</th>'
                                + '<th scope="col">Score</th><th scope="col">Validity</th><th scope="col">Status</th>'
                                + '<th scope="col">Duration</th><th scope="col">Distance</th><th scope="col">Events</th><th scope="col">Esc.</th></tr></thead>'
                                + '<tbody>' + sessions.map(renderDriverSessionRow).join("") + '</tbody></table></div>'
                        : '<p class="driver-panel-empty">No persisted sessions found for this driver.</p>')
                + '</article>';
    }

    function scorePointPath(points) {
        return points.map(function (point, index) {
            return (index === 0 ? "M " : "L ") + point.x.toFixed(2) + " " + point.y.toFixed(2);
        }).join(" ");
    }

    function renderScoreTrend(points) {
        var totalLabel = points.length + " " + pluralize(points.length, "day", "days");

        if (!points.length) {
            return '<article class="dashboard-panel driver-score-trend-panel" aria-labelledby="driver-inline-score-trend-title">'
                    + '<div class="panel-header refined"><div><h2 class="panel-title" id="driver-inline-score-trend-title">Score Trend</h2></div>'
                    + '<span class="panel-total">0 days</span></div>'
                    + '<div class="empty-panel refined-empty">No score trend days are available yet. Analytics will appear after session uploads.</div>'
                    + '</article>';
        }

        var width = Math.max(720, points.length * 74);
        var height = 286;
        var padding = {
            top: 24,
            right: 28,
            bottom: 52,
            left: 42
        };
        var drawableWidth = width - padding.left - padding.right;
        var drawableHeight = height - padding.top - padding.bottom;
        var divisor = Math.max(1, points.length - 1);
        var mapped = points.map(function (point, index) {
            var score = Math.max(0, Math.min(100, Number(point.averageScore || 0)));
            return {
                x: padding.left + (drawableWidth * index / divisor),
                y: padding.top + drawableHeight - (drawableHeight * score / 100),
                score: score,
                count: Number(point.sessionCount || 0),
                timestamp: point.timestamp
            };
        });
        var line = scorePointPath(mapped);
        var area = line + " L " + mapped[mapped.length - 1].x.toFixed(2) + " " + (height - padding.bottom)
                + " L " + mapped[0].x.toFixed(2) + " " + (height - padding.bottom) + " Z";
        var grid = [100, 75, 50, 25, 0].map(function (value) {
            var y = padding.top + drawableHeight - (drawableHeight * value / 100);
            return '<line x1="' + padding.left + '" y1="' + y.toFixed(2) + '" x2="' + (width - padding.right)
                    + '" y2="' + y.toFixed(2) + '" stroke="rgba(214,214,214,0.12)" stroke-width="1"></line>'
                    + '<text x="12" y="' + (y + 4).toFixed(2) + '" fill="rgba(137,153,173,0.9)" font-size="12" font-weight="700">'
                    + value + '</text>';
        }).join("");
        var labelStep = Math.max(1, Math.ceil(mapped.length / 8));
        var dots = mapped.map(function (point) {
            var tooltip = trendDate(point.timestamp, true) + "\nScore " + formatNumber(point.score, 1)
                    + "\n" + point.count + " " + pluralize(point.count, "session", "sessions");
            return '<circle class="trend-dot" cx="' + point.x.toFixed(2) + '" cy="' + point.y.toFixed(2)
                    + '" r="5"><title>' + escapeHtml(tooltip) + '</title></circle>';
        }).join("");
        var labels = mapped.map(function (point, index) {
            if (index % labelStep !== 0 && index !== mapped.length - 1) {
                return "";
            }
            return '<text x="' + point.x.toFixed(2) + '" y="' + (height - 16)
                    + '" fill="rgba(137,153,173,0.82)" font-size="11" font-weight="700" text-anchor="middle">'
                    + escapeHtml(trendDate(point.timestamp, false)) + '</text>';
        }).join("");
        var readouts = mapped.map(function (point) {
            return '<span role="listitem"><strong>' + escapeHtml(trendDate(point.timestamp, false)) + '</strong><small>'
                    + escapeHtml("Score " + formatNumber(point.score, 1) + ", " + point.count + " "
                            + pluralize(point.count, "session", "sessions"))
                    + '</small></span>';
        }).join("");

        return '<article class="dashboard-panel driver-score-trend-panel" aria-labelledby="driver-inline-score-trend-title">'
                + '<div class="panel-header refined"><div><h2 class="panel-title" id="driver-inline-score-trend-title">Score Trend</h2></div>'
                + '<span class="panel-total">' + escapeHtml(totalLabel) + '</span></div>'
                + '<div class="driver-line-chart" aria-label="Driver score trend chart">'
                + '<div class="chart-stage"><svg style="width:' + width + 'px;min-width:100%" viewBox="0 0 ' + width + ' ' + height + '" role="img" aria-label="Driver score trend">'
                + grid
                + '<path class="trend-area" d="' + area + '"></path>'
                + '<path class="trend-line" d="' + line + '"></path>'
                + dots
                + labels
                + '</svg></div>'
                + '<div class="trend-readouts" role="list" aria-label="Score trend daily values">' + readouts + '</div>'
                + '</div>'
                + '</article>';
    }

    function renderAnalyticsContext(data, context) {
        var reliability = Math.max(0, Math.min(100, Number(data.longTermReliabilityScore || 0)));
        return '<article class="analytics-context-panel">'
                + '<div class="analytics-context-copy">'
                + '<p class="analytics-kicker">Analytical context</p>'
                + '<h3>' + escapeHtml(context.driverName) + '</h3>'
                + '<p>Driver id ' + escapeHtml(context.driverId) + ' / All time</p>'
                + '</div>'
                + '<div class="analytics-context-metrics">'
                + '<div class="context-score"><span>Long-term reliability</span><strong>' + escapeHtml(formatNumber(reliability, 1)) + '</strong>'
                + '<span class="score-track wide" aria-hidden="true"><span style="width:' + reliability + '%"></span></span></div>'
                + '<div><span>Total sessions</span><strong>' + escapeHtml(data.totalSessions || 0) + '</strong></div>'
                + '<div><span>Total distance</span><strong>' + escapeHtml(formatNumber(data.totalDistanceKm, 1) + " km") + '</strong></div>'
                + '</div>'
                + '</article>';
    }

    function renderKpiCard(label, value, helper, tone) {
        return '<article class="analytics-kpi-card' + (tone ? " " + tone : "") + '">'
                + '<span class="kpi-label">' + escapeHtml(label) + '</span>'
                + '<strong>' + escapeHtml(value) + '</strong>'
                + '<span>' + escapeHtml(helper) + '</span>'
                + '</article>';
    }

    function renderAnalyticsKpis(data) {
        var total = Number(data.totalSessions || 0);
        return '<section class="driver-analytics-kpis" aria-label="Driver analytics KPI summary">'
                + renderKpiCard("Average final score", formatNumber(data.averageFinalScore, 1),
                        (data.completedSessionCount || 0) + " completed sessions", "score-health-card")
                + renderKpiCard("Valid sessions", data.validSessionCount || 0,
                        total > 0 ? formatPercent(data.validSessionCount || 0, total) + " of sessions" : "No session base", "")
                + renderKpiCard("Invalid sessions", data.invalidSessionCount || 0,
                        total > 0 ? formatPercent(data.invalidSessionCount || 0, total) + " of sessions" : "Trust signal pending", "warning-card")
                + renderKpiCard("Total events", data.totalEventCount || 0, "Grouped event aggregate", "")
                + renderKpiCard("Total escalations", data.totalEscalationCount || 0, "Grouped escalation aggregate", "escalation-card")
                + '</section>';
    }

    function renderSessionComposition(data) {
        var total = Number(data.totalSessions || 0);
        var valid = Number(data.validSessionCount || 0);
        var validPercent = total > 0 ? (valid * 100 / total).toFixed(0) : "0";
        var ring = total > 0
                ? 'background: conic-gradient(#9ee7c0 0 ' + validPercent + '%, rgba(255,155,155,0.95) 0 100%)'
                : 'background: conic-gradient(rgba(180,202,226,0.18) 0 100%)';

        return '<article class="dashboard-panel composition-panel" aria-labelledby="driver-inline-composition-title">'
                + '<div class="panel-header refined"><div><h2 class="panel-title" id="driver-inline-composition-title">Session Composition</h2></div></div>'
                + '<div class="analytics-composition">'
                + '<div class="composition-ring" style="' + ring + '" aria-hidden="true"><span>' + escapeHtml(total) + '</span></div>'
                + '<div class="composition-legend">'
                + '<div><span class="legend-dot success"></span><strong>' + escapeHtml(data.validSessionCount || 0) + '</strong><span>Valid</span></div>'
                + '<div><span class="legend-dot danger"></span><strong>' + escapeHtml(data.invalidSessionCount || 0) + '</strong><span>Invalid</span></div>'
                + '<div><span class="legend-dot info"></span><strong>' + escapeHtml(data.completedSessionCount || 0) + '</strong><span>Completed</span></div>'
                + '<div><span class="legend-dot warning"></span><strong>' + escapeHtml(data.abortedSessionCount || 0) + '</strong><span>Aborted</span></div>'
                + '</div>'
                + '</div>'
                + '</article>';
    }

    function renderCountBars(title, total, items, labelKey, emptyText, warning) {
        var rows = "";
        items.forEach(function (item) {
            var rawLabel = item[labelKey] || "Unknown";
            var count = Number(item.count || 0);
            var percent = total > 0 ? Math.max(3, (count / total) * 100) : 0;
            rows += '<div class="ranked-row" title="' + escapeHtml(rawLabel) + '">'
                    + '<div class="ranked-label"><span>' + escapeHtml(humanizeLabel(rawLabel)) + '</span><strong>' + escapeHtml(count) + '</strong></div>'
                    + '<div class="ranked-track" aria-hidden="true"><span class="ranked-fill" style="width: '
                    + percent.toFixed(1) + '%"></span></div>'
                    + '</div>';
        });

        return '<article class="dashboard-panel mix-panel" aria-labelledby="driver-inline-' + (warning ? "escalations" : "events") + '-title">'
                + '<div class="panel-header refined"><div><h2 class="panel-title" id="driver-inline-' + (warning ? "escalations" : "events") + '-title">'
                + escapeHtml(title) + '</h2></div><span class="panel-total' + (warning ? " warning" : "") + '">'
                + escapeHtml(total + (warning ? " escalations" : " events")) + '</span></div>'
                + (rows ? '<div class="ranked-bars driver-ranked-bars">' + rows + '</div>'
                        : '<div class="empty-panel refined-empty">' + escapeHtml(emptyText) + '</div>')
                + '</article>';
    }

    function renderEscalationCounts(total, items) {
        var rows = "";
        items.forEach(function (item) {
            var rawLabel = item.escalationType || "Unknown";
            var count = Number(item.count || 0);
            var percent = total > 0 ? (count * 100 / total).toFixed(0) : "0";
            rows += '<div class="escalation-radial-row" title="' + escapeHtml(rawLabel) + '">'
                    + '<span class="radial-meter" style="background: conic-gradient(#ffd394 0 ' + percent
                    + '%, rgba(180,202,226,0.13) 0 100%)" aria-hidden="true"></span>'
                    + '<span class="radial-label"><strong>' + escapeHtml(humanizeLabel(rawLabel)) + '</strong><small>'
                    + escapeHtml(count + " " + pluralize(count, "occurrence", "occurrences")) + '</small></span>'
                    + '</div>';
        });

        return '<article class="dashboard-panel mix-panel" aria-labelledby="driver-inline-escalations-title">'
                + '<div class="panel-header refined"><div><h2 class="panel-title" id="driver-inline-escalations-title">Grouped Escalations</h2></div>'
                + '<span class="panel-total warning">' + escapeHtml(total + " escalations") + '</span></div>'
                + (rows ? '<div class="escalation-radial-list">' + rows + '</div>'
                        : '<div class="empty-panel refined-empty">No grouped driver escalations are present yet.</div>')
                + '</article>';
    }

    function renderStatusSplit(data) {
        return '<article class="dashboard-panel compact-panel" aria-labelledby="driver-inline-breakdown-title">'
                + '<div class="panel-header refined"><div><h2 class="panel-title" id="driver-inline-breakdown-title">Validity / Completion</h2></div></div>'
                + '<div class="status-split two-by-two">'
                + '<div><span>Valid</span><strong>' + escapeHtml(data.validSessionCount || 0) + '</strong></div>'
                + '<div><span>Invalid</span><strong>' + escapeHtml(data.invalidSessionCount || 0) + '</strong></div>'
                + '<div><span>Completed</span><strong>' + escapeHtml(data.completedSessionCount || 0) + '</strong></div>'
                + '<div><span>Aborted</span><strong>' + escapeHtml(data.abortedSessionCount || 0) + '</strong></div>'
                + '</div>'
                + '</article>';
    }

    function renderAnalytics(data, includeTitle, item) {
        data = data || {};
        var context = driverPanelContext(item, data);
        var scoreTrend = Array.isArray(data.scoreTrend) ? data.scoreTrend : [];
        var eventCounts = Array.isArray(data.eventCounts) ? data.eventCounts : [];
        var escalationCounts = Array.isArray(data.escalationCounts) ? data.escalationCounts : [];
        var hasAnalytics = data.totalSessions > 0 || data.totalEventCount > 0 || data.totalEscalationCount > 0
                || scoreTrend.length > 0 || eventCounts.length > 0 || escalationCounts.length > 0;

        return '<section class="driver-subview-section driver-analytics-subview driver-analytics-page driver-analytics-embedded" aria-labelledby="driver-inline-analytics-title">'
                + '<header class="analytics-page-header driver-analytics-embedded-header"><div>'
                + '<p class="analytics-kicker">Dedicated driver analytics</p>'
                + '<h2 id="driver-inline-analytics-title">Driver Analytics</h2>'
                + '<p>' + escapeHtml(context.driverName + " / " + context.driverId) + '</p>'
                + '</div></header>'
                + renderAnalyticsContext(data, context)
                + renderAnalyticsKpis(data)
                + '<section class="driver-analytics-main-grid" aria-label="Primary driver analytics charts">'
                + renderScoreTrend(scoreTrend)
                + renderSessionComposition(data)
                + '</section>'
                + '<section class="driver-analytics-secondary-grid" aria-label="Grouped driver analytics">'
                + renderCountBars("Grouped Events", data.totalEventCount || 0, eventCounts, "eventType", "No grouped driver events are present yet.", false)
                + renderEscalationCounts(data.totalEscalationCount || 0, escalationCounts)
                + renderStatusSplit(data)
                + '</section>'
                + (!hasAnalytics ? '<section class="dashboard-zero-state"><h2>No analytical data yet</h2><p>Analytics will appear after session uploads.</p></section>' : "")
                + '</section>';
    }

    function renderPanelBody(panelType, data, includeTitle, item) {
        return panelType === "analytics" ? renderAnalytics(data, includeTitle, item) : renderDetails(data, includeTitle);
    }

    function setPanelHtml(container, html, animate) {
        if (!container) {
            return;
        }
        function scrollEmbeddedCharts() {
            window.requestAnimationFrame(function () {
                Array.prototype.slice.call(container.querySelectorAll(".driver-line-chart .chart-stage")).forEach(function (stage) {
                    stage.scrollLeft = stage.scrollWidth;
                });
            });
        }
        if (!animate) {
            container.innerHTML = html;
            scrollEmbeddedCharts();
            return;
        }
        container.classList.add("is-swapping");
        window.setTimeout(function () {
            container.innerHTML = html;
            scrollEmbeddedCharts();
            window.requestAnimationFrame(function () {
                container.classList.remove("is-swapping");
            });
        }, 90);
    }

    function syncPanelButtons(activeItem, panelType) {
        Array.prototype.slice.call(page.querySelectorAll("[data-driver-panel]")).forEach(function (button) {
            var item = button.closest("[data-driver-item]");
            var isActive = Boolean(activeItem && item === activeItem && button.getAttribute("data-driver-panel") === panelType);
            button.classList.toggle("is-active", isActive);
            button.setAttribute("aria-pressed", isActive ? "true" : "false");
        });
    }

    function collapseInlinePanel(immediate) {
        if (!activeInlinePanel) {
            return;
        }

        var state = activeInlinePanel;
        activeInlinePanel = null;
        state.panel.classList.remove("is-open", "is-swapping");
        state.row.classList.remove("is-open");
        syncPanelButtons(activeCardPanel ? activeCardPanel.item : null, activeCardPanel ? activeCardPanel.panelType : null);

        if (immediate) {
            state.panel.hidden = true;
            state.panel.innerHTML = "";
            state.row.hidden = true;
            return;
        }

        window.setTimeout(function () {
            if (!state.panel.classList.contains("is-open")) {
                state.panel.hidden = true;
                state.panel.innerHTML = "";
                state.row.hidden = true;
            }
        }, 260);
    }

    function positionCardOverlay(card) {
        if (!cardOverlay || !card) {
            return;
        }

        var rect = card.getBoundingClientRect();
        var margin = Math.max(16, Math.min(52, Math.round(window.innerWidth * 0.04)));
        var targetTop = Math.max(48, Math.min(82, Math.round(window.innerHeight * 0.06)));
        var targetHeight = Math.min(900, Math.max(420, window.innerHeight - targetTop - 18));
        cardOverlay.style.setProperty("--driver-card-start-top", rect.top + "px");
        cardOverlay.style.setProperty("--driver-card-start-left", rect.left + "px");
        cardOverlay.style.setProperty("--driver-card-start-width", rect.width + "px");
        cardOverlay.style.setProperty("--driver-card-start-height", rect.height + "px");
        cardOverlay.style.setProperty("--driver-card-target-top", targetTop + "px");
        cardOverlay.style.setProperty("--driver-card-target-left", margin + "px");
        cardOverlay.style.setProperty("--driver-card-target-width", Math.max(280, window.innerWidth - margin * 2) + "px");
        cardOverlay.style.setProperty("--driver-card-target-height", targetHeight + "px");
    }

    function renderCardOverlayShell(item, panelType, bodyHtml) {
        var name = item.getAttribute("data-driver-name") || "Driver";
        var driverId = item.getAttribute("data-driver-id") || "";
        var email = item.getAttribute("data-driver-email") || "";
        var initial = (name.trim().charAt(0) || "D").toUpperCase();

        return '<div class="driver-overlay-head">'
                + '<div class="driver-overlay-identity">'
                + '<span class="driver-avatar large">' + escapeHtml(initial) + '</span>'
                + '<div><span>' + escapeHtml(panelTitle(panelType)) + '</span><h3>' + escapeHtml(name) + '</h3>'
                + '<p><code>' + escapeHtml(driverId) + '</code>' + (email ? ' / ' + escapeHtml(email) : "") + '</p></div>'
                + '</div>'
                + '<button type="button" data-driver-panel-close aria-label="Close">X</button>'
                + '</div>'
                + '<div class="driver-overlay-body">' + bodyHtml + '</div>';
    }

    function collapseCardOverlay(immediate) {
        if (!activeCardPanel && (!cardOverlay || cardOverlay.hidden)) {
            return;
        }

        activeCardPanel = null;
        if (grid) {
            grid.classList.remove("has-card-overlay");
        }
        if (cardOverlay) {
            cardOverlay.classList.remove("is-open", "is-swapping");
            cardOverlay.removeAttribute("data-driver-panel-type");
        }
        if (cardBackdrop) {
            cardBackdrop.classList.remove("is-open");
        }
        syncPanelButtons(activeInlinePanel ? activeInlinePanel.item : null, activeInlinePanel ? activeInlinePanel.panelType : null);

        function finish() {
            if (cardOverlay && !activeCardPanel) {
                cardOverlay.hidden = true;
                cardOverlay.innerHTML = "";
            }
            if (cardBackdrop && !activeCardPanel) {
                cardBackdrop.hidden = true;
            }
            if (!activeCardPanel) {
                clearFocusedCard();
            }
        }

        if (immediate) {
            finish();
            return;
        }

        window.setTimeout(finish, 280);
    }

    function fetchPanel(item, panelType, requestId, onSuccess, onError) {
        window.fetch(panelUrl(item, panelType), { headers: { Accept: "application/json" } })
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error("Request failed");
                    }
                    return response.json();
                })
                .then(function (data) {
                    if (panelType !== "details") {
                        onSuccess(data, requestId);
                        return;
                    }
                    var driverId = data.driverId || item.getAttribute("data-driver-id") || "";
                    var size = Math.max(1, Number(data.totalSessions || 0), 100);
                    return window.fetch("/api/admin/drivers/" + encodeURIComponent(driverId)
                                    + "/sessions?page=0&size=" + encodeURIComponent(size)
                                    + "&sortBy=startTimestamp&sortDirection=desc", {
                        headers: { Accept: "application/json" }
                    })
                            .then(function (response) {
                                if (!response.ok) {
                                    throw new Error("Session request failed");
                                }
                                return response.json();
                            })
                            .then(function (sessionPage) {
                                data.sessionPage = sessionPage;
                                onSuccess(data, requestId);
                            })
                            .catch(function () {
                                data.sessionLoadError = true;
                                onSuccess(data, requestId);
                            });
                })
                .catch(function () {
                    onError(requestId);
                });
    }

    function showInlinePanel(item, panelType) {
        var row = getInlineRow(item);
        var panel = row && row.querySelector("[data-driver-inline-panel]");
        var requestId = String(++panelRequestSequence);

        if (!row || !panel) {
            return;
        }
        if (activeInlinePanel && activeInlinePanel.item === item && activeInlinePanel.panelType === panelType) {
            collapseInlinePanel(false);
            return;
        }

        collapseCardOverlay(false);
        if (activeInlinePanel && activeInlinePanel.item !== item) {
            collapseInlinePanel(false);
        }

        activeInlinePanel = {
            item: item,
            row: row,
            panel: panel,
            panelType: panelType,
            requestId: requestId
        };
        row.hidden = false;
        panel.hidden = false;
        row.classList.add("is-open");
        panel.setAttribute("data-driver-panel-type", panelType);
        syncPanelButtons(item, panelType);
        setPanelHtml(panel, renderPanelShell(panelType, renderPanelLoading(panelType, false)), panel.classList.contains("is-open"));
        window.requestAnimationFrame(function () {
            panel.classList.add("is-open");
        });

        fetchPanel(item, panelType, requestId, function (data, fetchedRequestId) {
            if (!activeInlinePanel || activeInlinePanel.requestId !== fetchedRequestId) {
                return;
            }
            setPanelHtml(panel, renderPanelShell(panelType, renderPanelBody(panelType, data, false, item)), true);
        }, function (failedRequestId) {
            if (!activeInlinePanel || activeInlinePanel.requestId !== failedRequestId) {
                return;
            }
            setPanelHtml(panel, renderPanelShell(panelType, renderPanelError(panelType, false)), true);
        });
    }

    function showCardPanel(item, panelType) {
        var requestId = String(++panelRequestSequence);

        if (!cardOverlay || !cardBackdrop) {
            return;
        }
        if (activeCardPanel && activeCardPanel.item === item && activeCardPanel.panelType === panelType) {
            collapseCardOverlay(false);
            return;
        }

        collapseInlinePanel(false);
        activeCardPanel = {
            item: item,
            panelType: panelType,
            requestId: requestId
        };
        focusCard(item);
        item.classList.add("is-expanded");
        if (grid) {
            grid.classList.add("has-card-overlay");
        }
        positionCardOverlay(item);
        cardBackdrop.hidden = false;
        cardOverlay.hidden = false;
        cardOverlay.setAttribute("data-driver-panel-type", panelType);
        syncPanelButtons(item, panelType);
        setPanelHtml(cardOverlay, renderCardOverlayShell(item, panelType, renderPanelLoading(panelType, true)), cardOverlay.classList.contains("is-open"));
        window.requestAnimationFrame(function () {
            cardBackdrop.classList.add("is-open");
            cardOverlay.classList.add("is-open");
        });

        fetchPanel(item, panelType, requestId, function (data, fetchedRequestId) {
            if (!activeCardPanel || activeCardPanel.requestId !== fetchedRequestId) {
                return;
            }
            setPanelHtml(cardOverlay, renderCardOverlayShell(item, panelType, renderPanelBody(panelType, data, true, item)), true);
        }, function (failedRequestId) {
            if (!activeCardPanel || activeCardPanel.requestId !== failedRequestId) {
                return;
            }
            setPanelHtml(cardOverlay, renderCardOverlayShell(item, panelType, renderPanelError(panelType, true)), true);
        });
    }

    function showPanel(item, panelType) {
        if (item.matches("[data-draggable-card]")) {
            showCardPanel(item, panelType);
        } else {
            showInlinePanel(item, panelType);
        }
    }

    function restoreManualOrder(state) {
        if (!grid || !state.manualOrder || !state.manualOrder.length) {
            return;
        }

        manualOrder = state.manualOrder;
        getItems(grid)
                .sort(function (left, right) {
                    var leftIndex = manualOrder.indexOf(left.getAttribute("data-driver-id"));
                    var rightIndex = manualOrder.indexOf(right.getAttribute("data-driver-id"));
                    return (leftIndex === -1 ? 9999 : leftIndex) - (rightIndex === -1 ? 9999 : rightIndex);
                })
                .forEach(function (item) {
                    grid.appendChild(item);
                });
    }

    function nearestCard(pointerX, pointerY, source) {
        var cards = getItems(grid).filter(function (card) {
            return card !== source && !card.hidden;
        });
        var nearest = null;
        var nearestDistance = Number.POSITIVE_INFINITY;

        cards.forEach(function (card) {
            var rect = card.getBoundingClientRect();
            var centerX = rect.left + rect.width / 2;
            var centerY = rect.top + rect.height / 2;
            var distance = Math.hypot(pointerX - centerX, pointerY - centerY);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = card;
            }
        });

        return nearest;
    }

    function setupDrag() {
        if (!grid) {
            return;
        }

        var drag = null;

        grid.addEventListener("pointerdown", function (event) {
            var card = event.target.closest("[data-draggable-card]");
            var interactive = event.target.closest("a, button, input, select, textarea, label");

            if (!card || interactive || event.button !== 0 || grid.classList.contains("has-card-overlay")) {
                return;
            }

            event.preventDefault();
            card.setPointerCapture(event.pointerId);
            drag = {
                source: card,
                pointerId: event.pointerId,
                startX: event.clientX,
                startY: event.clientY,
                ghost: card.cloneNode(true),
                target: null
            };
            drag.ghost.classList.add("driver-drag-ghost");
            drag.ghost.removeAttribute("tabindex");
            document.body.appendChild(drag.ghost);
            card.classList.add("is-source");
            grid.classList.add("is-dragging");
            moveGhost(event.clientX, event.clientY);
        });

        grid.addEventListener("pointermove", function (event) {
            if (!drag || event.pointerId !== drag.pointerId) {
                return;
            }

            moveGhost(event.clientX, event.clientY);
            if (drag.target) {
                drag.target.classList.remove("is-drop-target");
            }
            drag.target = nearestCard(event.clientX, event.clientY, drag.source);
            if (drag.target) {
                drag.target.classList.add("is-drop-target");
            }
        });

        grid.addEventListener("pointerup", finishDrag);
        grid.addEventListener("pointercancel", finishDrag);

        function moveGhost(x, y) {
            if (!drag || !drag.ghost) {
                return;
            }
            drag.ghost.style.left = x + "px";
            drag.ghost.style.top = y + "px";
        }

        function finishDrag(event) {
            if (!drag || event.pointerId !== drag.pointerId) {
                return;
            }

            var moved = Math.hypot(event.clientX - drag.startX, event.clientY - drag.startY);
            if (drag.target) {
                drag.target.classList.remove("is-drop-target");
            }
            if (drag.target && moved > 8) {
                swapCards(drag.source, drag.target);
            } else if (moved <= 8) {
                focusCard(drag.source);
            }

            drag.source.classList.remove("is-source");
            if (drag.ghost && drag.ghost.parentNode) {
                drag.ghost.parentNode.removeChild(drag.ghost);
            }
            grid.classList.remove("is-dragging");
            drag = null;
        }
    }

    var state = loadState();

    if (searchInput && state.search && !searchInput.value) {
        searchInput.value = state.search;
    }
    if (sortSelect && state.sort) {
        sortSelect.value = state.sort;
    }

    restoreManualOrder(state);
    setupDrag();

    [searchInput, sortSelect].filter(Boolean).forEach(function (control) {
        control.addEventListener("input", applyFiltersAndSort);
        control.addEventListener("change", applyFiltersAndSort);
    });

    if (controlsForm) {
        controlsForm.addEventListener("submit", function (event) {
            event.preventDefault();
            applyFiltersAndSort();
        });
    }

    modeButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            setMode(button.getAttribute("data-driver-mode"));
        });
    });

    page.addEventListener("click", function (event) {
        var closeButton = event.target.closest("[data-driver-panel-close]");
        var panelButton = event.target.closest("[data-driver-panel]");
        var backdrop = event.target.closest("[data-driver-card-backdrop]");

        if (backdrop) {
            collapseCardOverlay(false);
            return;
        }

        if (closeButton) {
            var row = closeButton.closest("[data-driver-inline-row]");
            if (row) {
                collapseInlinePanel(false);
            } else {
                collapseCardOverlay(false);
            }
            return;
        }

        if (panelButton) {
            var item = panelButton.closest("[data-driver-item]");
            if (item) {
                showPanel(item, panelButton.getAttribute("data-driver-panel"));
            }
        }
    });

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            collapseInlinePanel(false);
            collapseCardOverlay(false);
        }
    });

    var settingsDefaultMode = window.Phase2AdminSettings
            ? window.Phase2AdminSettings.load().driversDefaultView
            : "list";
    setMode(state.mode || settingsDefaultMode || "list");
}());

(function () {
    var charts = Array.prototype.slice.call(document.querySelectorAll("[data-driver-score-chart]"));

    if (!charts.length) {
        return;
    }

    function clampScore(value) {
        if (Number.isNaN(value)) {
            return 0;
        }
        return Math.max(0, Math.min(100, value));
    }

    function pointPath(points) {
        return points.map(function (point, index) {
            return (index === 0 ? "M " : "L ") + point.x.toFixed(2) + " " + point.y.toFixed(2);
        }).join(" ");
    }

    function formatTrendDate(value, includeTime) {
        var number = Number(value);
        if (!Number.isFinite(number) || number <= 0) {
            return "Not available";
        }
        var date = new Date(number > 1000000000000 ? number : number * 1000);
        var options = includeTime
                ? { year: "numeric", month: "short", day: "2-digit", hour: "2-digit", minute: "2-digit" }
                : { month: "short", day: "2-digit" };
        return new Intl.DateTimeFormat(undefined, options).format(date);
    }

    function pluralize(value, singular, plural) {
        return Number(value || 0) === 1 ? singular : plural;
    }

    function renderChart(chart) {
        var stage = chart.querySelector("[data-chart-stage]");
        var nodes = Array.prototype.slice.call(chart.querySelectorAll("[data-trend-point]"));

        if (!stage || !nodes.length) {
            return;
        }

        var width = Math.max(720, nodes.length * 74);
        var height = 286;
        var padding = {
            top: 24,
            right: 28,
            bottom: 52,
            left: 42
        };
        var drawableWidth = width - padding.left - padding.right;
        var drawableHeight = height - padding.top - padding.bottom;
        var divisor = Math.max(1, nodes.length - 1);
        var points = nodes.map(function (node, index) {
            var score = clampScore(parseFloat(node.getAttribute("data-score") || "0"));
            return {
                x: padding.left + (drawableWidth * index / divisor),
                y: padding.top + drawableHeight - (drawableHeight * score / 100),
                score: score,
                count: node.getAttribute("data-count") || "0",
                label: node.getAttribute("data-label") || ""
            };
        });
        var line = pointPath(points);
        var area = line + " L " + points[points.length - 1].x.toFixed(2) + " " + (height - padding.bottom)
                + " L " + points[0].x.toFixed(2) + " " + (height - padding.bottom) + " Z";
        var grid = [100, 75, 50, 25, 0].map(function (value) {
            var y = padding.top + drawableHeight - (drawableHeight * value / 100);
            return '<line x1="' + padding.left + '" y1="' + y.toFixed(2) + '" x2="' + (width - padding.right)
                    + '" y2="' + y.toFixed(2) + '" stroke="rgba(214,214,214,0.12)" stroke-width="1"/>'
                    + '<text x="12" y="' + (y + 4).toFixed(2) + '" fill="rgba(137,153,173,0.9)" font-size="12" font-weight="700">'
                    + value + '</text>';
        }).join("");
        var dots = points.map(function (point) {
            var tooltip = formatTrendDate(point.label, true) + "\nScore " + point.score.toFixed(1)
                    + "\n" + point.count + " " + pluralize(point.count, "session", "sessions");
            return '<g><circle class="trend-dot" cx="' + point.x.toFixed(2) + '" cy="' + point.y.toFixed(2)
                    + '" r="5"><title>' + tooltip + '</title></circle></g>';
        }).join("");
        var labelStep = Math.max(1, Math.ceil(points.length / 8));
        var labels = points.map(function (point, index) {
            if (index % labelStep !== 0 && index !== points.length - 1) {
                return "";
            }
            return '<text x="' + point.x.toFixed(2) + '" y="' + (height - 16)
                    + '" fill="rgba(137,153,173,0.82)" font-size="11" font-weight="700" text-anchor="middle">'
                    + formatTrendDate(point.label, false) + '</text>';
        }).join("");
        var readouts = chart.querySelector("[data-trend-readouts]");
        if (readouts) {
            readouts.innerHTML = points.map(function (point) {
                return '<span role="listitem"><strong>' + formatTrendDate(point.label, false) + '</strong><small>'
                        + 'Score ' + point.score.toFixed(1) + ', ' + point.count + ' '
                        + pluralize(point.count, "session", "sessions") + '</small></span>';
            }).join("");
        }

        stage.innerHTML = '<svg style="width:' + width + 'px;min-width:100%" viewBox="0 0 ' + width + ' ' + height + '" role="img" aria-label="Average score trend">'
                + grid
                + '<path class="trend-area" d="' + area + '"></path>'
                + '<path class="trend-line" d="' + line + '"></path>'
                + dots
                + labels
                + '</svg>';
        stage.scrollLeft = stage.scrollWidth;
    }

    charts.forEach(renderChart);
}());

(function () {
    var charts = Array.prototype.slice.call(document.querySelectorAll("[data-fleet-score-chart]"));

    if (!charts.length) {
        return;
    }

    function clampScore(value) {
        if (Number.isNaN(value)) {
            return 0;
        }
        return Math.max(0, Math.min(100, value));
    }

    function pointPath(points) {
        return points.map(function (point, index) {
            return (index === 0 ? "M " : "L ") + point.x.toFixed(2) + " " + point.y.toFixed(2);
        }).join(" ");
    }

    function escapeHtml(value) {
        return String(value || "")
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;");
    }

    function formatEpoch(value) {
        var number = Number(value);
        if (!Number.isFinite(number) || number <= 0) {
            return "Not available";
        }
        var date = new Date(number > 1000000000000 ? number : number * 1000);
        return new Intl.DateTimeFormat(undefined, {
            year: "numeric",
            month: "short",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit"
        }).format(date);
    }

    function renderChart(chart) {
        var stage = chart.querySelector("[data-fleet-chart-stage]");
        var nodes = Array.prototype.slice.call(chart.querySelectorAll("[data-fleet-trend-point]"));

        if (!stage || !nodes.length) {
            return;
        }

        var width = Math.max(760, nodes.length * 54);
        var height = 320;
        var padding = {
            top: 24,
            right: 28,
            bottom: 48,
            left: 42
        };
        var drawableWidth = width - padding.left - padding.right;
        var drawableHeight = height - padding.top - padding.bottom;
        var divisor = Math.max(1, nodes.length - 1);
        var points = nodes.map(function (node, index) {
            var score = clampScore(parseFloat(node.getAttribute("data-score") || "0"));
            return {
                x: padding.left + (drawableWidth * index / divisor),
                y: padding.top + drawableHeight - (drawableHeight * score / 100),
                score: score,
                sessionId: node.getAttribute("data-session-id") || "",
                driverId: node.getAttribute("data-driver-id") || "",
                driverName: node.getAttribute("data-driver-name") || "",
                href: node.getAttribute("data-href") || "",
                label: node.getAttribute("data-label") || ""
            };
        });
        var line = pointPath(points);
        var area = line + " L " + points[points.length - 1].x.toFixed(2) + " " + (height - padding.bottom)
                + " L " + points[0].x.toFixed(2) + " " + (height - padding.bottom) + " Z";
        var grid = [100, 75, 50, 25].map(function (value) {
            var y = padding.top + drawableHeight - (drawableHeight * value / 100);
            return '<line x1="' + padding.left + '" y1="' + y.toFixed(2) + '" x2="' + (width - padding.right)
                    + '" y2="' + y.toFixed(2) + '" stroke="rgba(214,214,214,0.12)" stroke-width="1"/>'
                    + '<text x="12" y="' + (y + 4).toFixed(2) + '" fill="rgba(137,153,173,0.9)" font-size="12" font-weight="700">'
                    + value + '</text>';
        }).join("");
        var dots = points.map(function (point) {
            var driverLabel = point.driverName || point.driverId || "Unknown driver";
            var sessionLabel = point.sessionId && point.sessionId.length > 16
                    ? point.sessionId.slice(0, 8) + "..." + point.sessionId.slice(-4)
                    : point.sessionId;
            var tooltip = driverLabel + "\n" + (sessionLabel || "Unknown session")
                    + "\nScore " + point.score.toFixed(1) + "\n" + formatEpoch(point.label);
            var href = escapeHtml(point.href);
            return '<a href="' + href + '" aria-label="Open session ' + escapeHtml(sessionLabel || point.sessionId) + '">'
                    + '<circle class="trend-dot fleet-session-dot" cx="' + point.x.toFixed(2) + '" cy="' + point.y.toFixed(2)
                    + '" r="6"><title>' + escapeHtml(tooltip) + '</title></circle></a>';
        }).join("");
        var labels = points.map(function (point, index) {
            if (index % Math.ceil(points.length / 8) !== 0 && index !== points.length - 1) {
                return "";
            }
            return '<text x="' + point.x.toFixed(2) + '" y="' + (height - 14)
                    + '" fill="rgba(137,153,173,0.82)" font-size="11" font-weight="700" text-anchor="middle">'
                    + escapeHtml(formatEpoch(point.label).split(",")[0])
                    + '</text>';
        }).join("");

        stage.innerHTML = '<svg viewBox="0 0 ' + width + ' ' + height + '" role="img" aria-label="Fleet average score trend">'
                + grid
                + '<path class="trend-area" d="' + area + '"></path>'
                + '<path class="trend-line" d="' + line + '"></path>'
                + dots
                + labels
                + '</svg>';
    }

    charts.forEach(renderChart);

    Array.prototype.slice.call(document.querySelectorAll("[data-fleet-score-panel]")).forEach(function (panel) {
        var close = panel.querySelector("[data-fleet-score-close]");

        panel.addEventListener("click", function (event) {
            if (event.target.closest("a") || event.target.closest("[data-fleet-score-close]")) {
                return;
            }
            panel.classList.add("is-expanded");
        });

        panel.addEventListener("keydown", function (event) {
            if (event.key === "Enter" || event.key === " ") {
                event.preventDefault();
                panel.classList.add("is-expanded");
            }
        });

        if (close) {
            close.addEventListener("click", function (event) {
                event.stopPropagation();
                panel.classList.remove("is-expanded");
            });
        }
    });
}());

(function () {
    var charts = Array.prototype.slice.call(document.querySelectorAll("[data-fleet-pie-chart]"));

    function cssColor(name, fallback) {
        var value = window.getComputedStyle(document.documentElement).getPropertyValue(name).trim();
        return value || fallback;
    }

    function chartColors() {
        return [
            cssColor("--chart-green", "#2FA866"),
            cssColor("--chart-yellow", "#D6A21F"),
            cssColor("--chart-red", "#D84A3A"),
            cssColor("--chart-orange", "#C46A2B"),
            cssColor("--chart-grey", "#8A8A8A"),
            cssColor("--chart-white", "#E8E8E8"),
            cssColor("--chart-green", "#2FA866"),
            cssColor("--accent-grey-medium", "#9A9A9A")
        ];
    }

    function escapeHtml(value) {
        return String(value || "")
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;");
    }

    function shortPieLabel(label) {
        var normalized = String(label || "").trim().replace(/\s+/g, "_").toUpperCase();
        var labels = {
            HARSH_BRAKING: "Harsh Braking",
            LANE_DEPARTURE: "Lane Departure",
            HIGH_FATIGUE_EPISODE: "Fatigue",
            EMOTIONAL_DISTRESS_EPISODE: "Emotional Distress",
            OBSTACLE_NEAR_MISS: "Near Miss",
            REPEATED_HARSH_BRAKING: "Repeated Braking",
            FATIGUE_WITH_LANE_DEVIATION: "Fatigue + Lane",
            PROLONGED_LANE_DRIFT: "Lane Drift",
            MULTIPLE_EVENTS_SHORT_WINDOW: "Multi-Event Window"
        };
        if (labels[normalized]) {
            return labels[normalized];
        }
        return String(label || "").toLowerCase().replace(/\b\w/g, function (letter) {
            return letter.toUpperCase();
        });
    }

    charts.forEach(function (chart) {
        var figure = chart.querySelector("[data-fleet-pie-figure]");
        var legend = chart.querySelector("[data-fleet-pie-legend]");
        var slices = Array.prototype.slice.call(chart.querySelectorAll("[data-fleet-pie-slice]")).map(function (node) {
            return {
                label: node.getAttribute("data-label") || "",
                count: Number(node.getAttribute("data-count") || "0"),
                total: Number(node.getAttribute("data-total") || "0")
            };
        }).filter(function (slice) {
            return slice.count > 0 && slice.total > 0;
        });

        if (!figure || !legend || !slices.length) {
            return;
        }

        var colors = chartColors();
        var cursor = 0;
        var gradients = slices.map(function (slice, index) {
            var percent = slice.count * 100 / slice.total;
            var start = cursor;
            cursor += percent;
            return colors[index % colors.length] + " " + start.toFixed(2) + "% " + cursor.toFixed(2) + "%";
        });

        figure.style.background = "conic-gradient(" + gradients.join(", ") + ")";
        legend.innerHTML = slices.map(function (slice, index) {
            var percent = slice.count * 100 / slice.total;
            var displayLabel = shortPieLabel(slice.label);
            return '<div class="fleet-pie-legend-row" title="' + escapeHtml(slice.label) + '"><span class="fleet-pie-swatch" style="background:'
                    + colors[index % colors.length] + '"></span><strong>' + escapeHtml(displayLabel)
                    + '</strong><span class="fleet-pie-count">' + slice.count + '</span><span class="fleet-pie-percent">'
                    + percent.toFixed(1) + '%</span></div>';
        }).join("");
    });
}());

(function () {
    var page = document.querySelector("[data-sessions-page]");

    if (!page) {
        return;
    }

    var searchInput = page.querySelector("[data-session-search]");
    var sortSelect = page.querySelector("[data-session-sort]");
    var sortByInput = page.querySelector("[data-session-sort-by]");
    var sortDirectionInput = page.querySelector("[data-session-sort-direction]");
    var rows = Array.prototype.slice.call(page.querySelectorAll("[data-session-row]"));
    var noResults = page.querySelector("[data-session-no-results]");
    var visibleCount = page.querySelector("[data-session-visible-count]");

    function epochToDate(value) {
        var number = Number(value);
        if (!Number.isFinite(number) || number <= 0) {
            return null;
        }
        return new Date(number > 1000000000000 ? number : number * 1000);
    }

    function formatEpoch(value) {
        var date = epochToDate(value);
        if (!date) {
            return "Not available";
        }
        return new Intl.DateTimeFormat(undefined, {
            year: "numeric",
            month: "short",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit"
        }).format(date);
    }

    function formatDuration(secondsValue) {
        var seconds = Number(secondsValue);
        if (!Number.isFinite(seconds) || seconds < 0) {
            return "0s";
        }
        var hours = Math.floor(seconds / 3600);
        var minutes = Math.floor((seconds % 3600) / 60);
        var remaining = Math.floor(seconds % 60);

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        if (minutes > 0) {
            return minutes + "m " + remaining + "s";
        }
        return remaining + "s";
    }

    function syncSortInputs() {
        if (!sortSelect || !sortByInput || !sortDirectionInput) {
            return;
        }
        var parts = (sortSelect.value || "startTimestamp:desc").split(":");
        sortByInput.value = parts[0] || "startTimestamp";
        sortDirectionInput.value = parts[1] || "desc";
    }

    function rowMatches(row) {
        var query = searchInput ? searchInput.value.trim().toLowerCase() : "";
        var haystack = [
            row.getAttribute("data-session-id"),
            row.getAttribute("data-driver-id"),
            row.getAttribute("data-driver-name"),
            row.getAttribute("data-vehicle-id")
        ].join(" ").toLowerCase();

        return !query || haystack.indexOf(query) !== -1;
    }

    function updateSummary() {
        var visibleRows = rows.filter(function (row) {
            return !row.hidden;
        });
        var counts = {
            VALID: 0,
            INVALID: 0
        };

        visibleRows.forEach(function (row) {
            var validity = row.getAttribute("data-validity");
            if (Object.prototype.hasOwnProperty.call(counts, validity)) {
                counts[validity] += 1;
            }
        });

        if (visibleCount) {
            visibleCount.textContent = String(visibleRows.length);
        }

        Object.keys(counts).forEach(function (key) {
            var node = page.querySelector('[data-session-count="' + key + '"]');
            if (node) {
                node.textContent = String(counts[key]);
            }
        });

        if (noResults) {
            noResults.hidden = rows.length === 0 || visibleRows.length !== 0;
        }
    }

    function applySearch() {
        rows.forEach(function (row) {
            row.hidden = !rowMatches(row);
        });
        updateSummary();
    }

    page.querySelectorAll("[data-epoch]").forEach(function (node) {
        node.textContent = formatEpoch(node.getAttribute("data-epoch"));
    });

    page.querySelectorAll("[data-duration-label]").forEach(function (node) {
        node.textContent = formatDuration(node.getAttribute("data-duration-label"));
    });

    if (sortSelect) {
        sortSelect.addEventListener("change", syncSortInputs);
        syncSortInputs();
    }

    if (searchInput) {
        searchInput.addEventListener("input", applySearch);
    }

    applySearch();
}());

(function () {
    var page = document.querySelector("[data-vehicles-page]");

    if (!page) {
        return;
    }

    var storageKey = "phase2.admin.vehicles";
    var searchInput = page.querySelector("[data-vehicle-search]");
    var sortSelect = page.querySelector("[data-vehicle-sort]");
    var sortByInput = page.querySelector("[data-vehicle-sort-by]");
    var sortDirectionInput = page.querySelector("[data-vehicle-sort-direction]");
    var rows = Array.prototype.slice.call(page.querySelectorAll("[data-vehicle-row]"));
    var noResults = page.querySelector("[data-vehicle-no-results]");
    var visibleCount = page.querySelector("[data-vehicle-visible-count]");
    var expandedRow = null;

    function loadState() {
        try {
            return JSON.parse(window.localStorage.getItem(storageKey) || "{}");
        } catch (error) {
            return {};
        }
    }

    function saveState(extra) {
        var state = loadState();
        Object.keys(extra).forEach(function (key) {
            state[key] = extra[key];
        });
        window.localStorage.setItem(storageKey, JSON.stringify(state));
    }

    function epochToDate(value) {
        var number = Number(value);
        if (!Number.isFinite(number) || number <= 0) {
            return null;
        }
        return new Date(number > 1000000000000 ? number : number * 1000);
    }

    function formatEpoch(value) {
        var date = epochToDate(value);
        if (!date) {
            return "Not available";
        }
        return new Intl.DateTimeFormat(undefined, {
            year: "numeric",
            month: "short",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit"
        }).format(date);
    }

    function escapeHtml(value) {
        return String(value == null ? "" : value)
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#39;");
    }

    function formatDuration(seconds) {
        var total = Number(seconds || 0);
        if (!Number.isFinite(total) || total <= 0) {
            return "0s";
        }
        var hours = Math.floor(total / 3600);
        var minutes = Math.floor((total % 3600) / 60);
        var remaining = Math.floor(total % 60);
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        if (minutes > 0) {
            return minutes + "m " + remaining + "s";
        }
        return remaining + "s";
    }

    function getHistoryRow(row) {
        return row && row.nextElementSibling && row.nextElementSibling.matches("[data-vehicle-history-row]")
                ? row.nextElementSibling
                : null;
    }

    function setSummary(row, sessions, totalItems) {
        var count = row.querySelector("[data-vehicle-session-count]");
        var lastUsed = row.querySelector("[data-vehicle-last-used]");
        var recentDriver = row.querySelector("[data-vehicle-recent-driver]");
        var latest = sessions.length ? sessions[0] : null;

        if (count) {
            count.textContent = String(totalItems || sessions.length || 0);
            count.classList.remove("vehicle-null-state");
        }
        if (lastUsed) {
            lastUsed.textContent = latest ? formatEpoch(latest.endTimestamp || latest.uploadedAt || latest.startTimestamp) : "No sessions";
            lastUsed.classList.toggle("vehicle-null-state", !latest);
        }
        if (recentDriver) {
            recentDriver.textContent = latest ? (latest.driverName || latest.driverId || "Driver id unavailable") : "No sessions";
            recentDriver.classList.toggle("vehicle-null-state", !latest);
        }
    }

    function renderSession(session) {
        var driverLabel = session.driverName || session.driverId || "Driver id unavailable";
        var returnTo = encodeURIComponent(window.location.pathname + window.location.search);
        var href = "/admin/sessions/" + encodeURIComponent(session.sessionId) + "?returnTo=" + returnTo;
        return '<a class="vehicle-history-session" href="' + href + '">'
                + '<span><strong>' + escapeHtml(session.sessionId) + '</strong><small>' + escapeHtml(driverLabel) + '</small></span>'
                + '<span><small>Driver id</small><strong>' + escapeHtml(session.driverId || "Unavailable") + '</strong></span>'
                + '<span><small>Score</small><strong>' + escapeHtml(Number(session.finalScore || 0).toFixed(1)) + '</strong></span>'
                + '<span><small>Validity</small><strong>' + escapeHtml(session.validity || "Unknown") + '</strong></span>'
                + '<span><small>Start / end</small><strong>' + escapeHtml(formatEpoch(session.startTimestamp)) + '</strong><small>' + escapeHtml(formatEpoch(session.endTimestamp)) + '</small></span>'
                + '<span><small>Duration</small><strong>' + escapeHtml(formatDuration(session.totalDurationSeconds)) + '</strong></span>'
                + '<span><small>Events</small><strong>' + escapeHtml(session.totalEventCount || 0) + '</strong></span>'
                + '<span><small>Escalations</small><strong>' + escapeHtml(session.totalEscalationCount || 0) + '</strong></span>'
                + '</a>';
    }

    function renderHistory(row, historyRow, data) {
        var panel = historyRow.querySelector("[data-vehicle-history-panel]");
        var sessions = data && Array.isArray(data.items) ? data.items : [];
        setSummary(row, sessions, data ? data.totalItems : 0);
        if (!panel) {
            return;
        }
        panel.innerHTML = '<div class="vehicle-history-head"><strong>Session history</strong>'
                + '<button class="vehicle-collapse-button" type="button" data-vehicle-collapse aria-label="Collapse vehicle history">X</button></div>'
                + (sessions.length
                        ? '<div class="vehicle-history-list">' + sessions.map(renderSession).join("") + '</div>'
                        : '<p class="vehicle-history-empty">No persisted sessions found for this vehicle.</p>');
    }

    function loadHistory(row, historyRow) {
        var vehicleId = row.getAttribute("data-vehicle-id");
        var panel = historyRow.querySelector("[data-vehicle-history-panel]");
        if (!vehicleId || !panel || historyRow.getAttribute("data-loaded") === "true") {
            return;
        }

        panel.innerHTML = '<div class="vehicle-history-head"><strong>Session history</strong>'
                + '<button class="vehicle-collapse-button" type="button" data-vehicle-collapse aria-label="Collapse vehicle history">X</button></div>'
                + '<p class="vehicle-history-empty">Loading...</p>';

        window.fetch("/api/admin/sessions?page=0&size=100&sortBy=startTimestamp&sortDirection=desc&vehicleId=" + encodeURIComponent(vehicleId), {
            headers: { Accept: "application/json" }
        })
                .then(function (response) {
                    if (!response.ok) {
                        throw new Error("Request failed");
                    }
                    return response.json();
                })
                .then(function (data) {
                    historyRow.setAttribute("data-loaded", "true");
                    renderHistory(row, historyRow, data);
                })
                .catch(function () {
                    panel.innerHTML = '<div class="vehicle-history-head"><strong>Session history</strong>'
                            + '<button class="vehicle-collapse-button" type="button" data-vehicle-collapse aria-label="Collapse vehicle history">X</button></div>'
                            + '<p class="vehicle-history-empty">Session history could not be loaded.</p>';
                });
    }

    function collapseRow(row) {
        var historyRow = getHistoryRow(row);
        if (historyRow) {
            historyRow.hidden = true;
        }
        row.classList.remove("is-expanded");
        var button = row.querySelector("[data-vehicle-expand]");
        if (button) {
            button.textContent = "Expand";
            button.setAttribute("aria-expanded", "false");
        }
        if (expandedRow === row) {
            expandedRow = null;
        }
    }

    function expandRow(row) {
        var historyRow = getHistoryRow(row);
        if (!historyRow) {
            return;
        }
        if (expandedRow && expandedRow !== row) {
            collapseRow(expandedRow);
        }
        var shouldOpen = historyRow.hidden;
        if (!shouldOpen) {
            collapseRow(row);
            return;
        }
        historyRow.hidden = false;
        row.classList.add("is-expanded");
        expandedRow = row;
        var button = row.querySelector("[data-vehicle-expand]");
        if (button) {
            button.textContent = "Collapse";
            button.setAttribute("aria-expanded", "true");
        }
        loadHistory(row, historyRow);
    }

    function syncSortInputs() {
        if (!sortSelect || !sortByInput || !sortDirectionInput) {
            return;
        }
        var parts = (sortSelect.value || "displayName:asc").split(":");
        sortByInput.value = parts[0] || "displayName";
        sortDirectionInput.value = parts[1] || "asc";
    }

    function rowMatches(row) {
        var query = searchInput ? searchInput.value.trim().toLowerCase() : "";
        var haystack = [
            row.getAttribute("data-vehicle-id"),
            row.getAttribute("data-vehicle-name")
        ].join(" ").toLowerCase();

        return !query || haystack.indexOf(query) !== -1;
    }

    function compareRows(left, right) {
        var parts = (sortSelect && sortSelect.value ? sortSelect.value : "displayName:asc").split(":");
        var field = parts[0] || "displayName";
        var direction = parts[1] === "desc" ? -1 : 1;
        var leftValue;
        var rightValue;

        if (field === "displayName") {
            leftValue = left.getAttribute("data-sort-name") || "";
            rightValue = right.getAttribute("data-sort-name") || "";
            return leftValue.localeCompare(rightValue) * direction;
        }

        if (field === "id") {
            leftValue = left.getAttribute("data-sort-id") || "";
            rightValue = right.getAttribute("data-sort-id") || "";
            return leftValue.localeCompare(rightValue) * direction;
        }

        leftValue = Number(left.getAttribute("data-sort-" + field) || "0");
        rightValue = Number(right.getAttribute("data-sort-" + field) || "0");
        return (leftValue - rightValue) * direction;
    }

    function updateSummary() {
        var visibleRows = rows.filter(function (row) {
            return !row.hidden;
        });
        if (visibleCount) {
            visibleCount.textContent = String(visibleRows.length);
        }

        if (noResults) {
            noResults.hidden = rows.length === 0 || visibleRows.length !== 0;
        }
    }

    function applyFiltersAndSort() {
        var body = page.querySelector("tbody");
        if (body) {
            rows.sort(compareRows).forEach(function (row) {
                var historyRow = getHistoryRow(row);
                body.appendChild(row);
                if (historyRow) {
                    body.appendChild(historyRow);
                }
                var hidden = !rowMatches(row);
                row.hidden = hidden;
                if (hidden && historyRow) {
                    collapseRow(row);
                }
            });
        }

        syncSortInputs();
        updateSummary();
        saveState({
            search: searchInput ? searchInput.value : "",
            sort: sortSelect ? sortSelect.value : "displayName:asc"
        });
    }

    var state = loadState();

    if (searchInput && state.search && !searchInput.value) {
        searchInput.value = state.search;
    }
    if (sortSelect && state.sort && (!sortSelect.value || sortSelect.value === "displayName:asc")) {
        sortSelect.value = state.sort;
    }

    [searchInput, sortSelect].filter(Boolean).forEach(function (control) {
        control.addEventListener("input", applyFiltersAndSort);
        control.addEventListener("change", applyFiltersAndSort);
    });

    page.addEventListener("click", function (event) {
        var collapse = event.target.closest("[data-vehicle-collapse]");
        if (collapse) {
            var historyRow = collapse.closest("[data-vehicle-history-row]");
            var row = historyRow ? historyRow.previousElementSibling : null;
            if (row) {
                collapseRow(row);
            }
            return;
        }

        var row = event.target.closest("[data-vehicle-row]");
        if (row && !event.target.closest("a")) {
            expandRow(row);
        }
    });

    page.addEventListener("keydown", function (event) {
        var row = event.target.closest("[data-vehicle-row]");
        if (row && (event.key === "Enter" || event.key === " ")) {
            event.preventDefault();
            expandRow(row);
        }
    });

    syncSortInputs();
    applyFiltersAndSort();
}());

(function () {
    var page = document.querySelector("[data-session-detail-page]");

    if (!page) {
        return;
    }

    var tabTriggers = Array.prototype.slice.call(page.querySelectorAll("[data-session-tab-trigger]"));
    var tabPanels = Array.prototype.slice.call(page.querySelectorAll("[data-session-tab-panel]"));

    function epochToDate(value) {
        var number = Number(value);
        if (!Number.isFinite(number) || number <= 0) {
            return null;
        }
        return new Date(number > 1000000000000 ? number : number * 1000);
    }

    function formatEpoch(value) {
        var date = epochToDate(value);
        if (!date) {
            return "Not available";
        }
        return new Intl.DateTimeFormat(undefined, {
            year: "numeric",
            month: "short",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit"
        }).format(date);
    }

    function formatDuration(secondsValue) {
        var seconds = Number(secondsValue);
        if (!Number.isFinite(seconds) || seconds < 0) {
            return "0s";
        }
        var hours = Math.floor(seconds / 3600);
        var minutes = Math.floor((seconds % 3600) / 60);
        var remaining = Math.floor(seconds % 60);

        if (hours > 0) {
            return hours + "h " + minutes + "m";
        }
        if (minutes > 0) {
            return minutes + "m " + remaining + "s";
        }
        return remaining + "s";
    }

    function setActiveTab(name) {
        tabTriggers.forEach(function (trigger) {
            var active = trigger.getAttribute("data-session-tab-trigger") === name;
            trigger.classList.toggle("is-active", active);
            trigger.setAttribute("aria-selected", active ? "true" : "false");
            trigger.setAttribute("tabindex", active ? "0" : "-1");
        });

        tabPanels.forEach(function (panel) {
            var active = panel.getAttribute("data-session-tab-panel") === name;
            panel.classList.toggle("is-active", active);
            panel.hidden = !active;
        });
    }

    function clampScore(value) {
        if (Number.isNaN(value)) {
            return 0;
        }
        return Math.max(0, Math.min(100, value));
    }

    function pointPath(points) {
        return points.map(function (point, index) {
            return (index === 0 ? "M " : "L ") + point.x.toFixed(2) + " " + point.y.toFixed(2);
        }).join(" ");
    }

    function renderChart(chart) {
        var stage = chart.querySelector("[data-session-chart-stage]");
        var nodes = Array.prototype.slice.call(chart.querySelectorAll("[data-session-score-point]"));

        if (!stage || !nodes.length) {
            return;
        }

        var width = 720;
        var height = 286;
        var padding = {
            top: 24,
            right: 28,
            bottom: 34,
            left: 38
        };
        var drawableWidth = width - padding.left - padding.right;
        var drawableHeight = height - padding.top - padding.bottom;
        var divisor = Math.max(1, nodes.length - 1);
        var points = nodes.map(function (node, index) {
            var score = clampScore(parseFloat(node.getAttribute("data-score") || "0"));
            var timestamp = node.getAttribute("data-timestamp");
            return {
                x: padding.left + (drawableWidth * index / divisor),
                y: padding.top + drawableHeight - (drawableHeight * score / 100),
                score: score,
                label: formatEpoch(timestamp)
            };
        });
        var line = pointPath(points);
        var area = line + " L " + points[points.length - 1].x.toFixed(2) + " " + (height - padding.bottom)
                + " L " + points[0].x.toFixed(2) + " " + (height - padding.bottom) + " Z";
        var grid = [100, 75, 50, 25].map(function (value) {
            var y = padding.top + drawableHeight - (drawableHeight * value / 100);
            return '<line x1="' + padding.left + '" y1="' + y.toFixed(2) + '" x2="' + (width - padding.right)
                    + '" y2="' + y.toFixed(2) + '" stroke="rgba(214,214,214,0.12)" stroke-width="1"/>'
                    + '<text x="12" y="' + (y + 4).toFixed(2) + '" fill="rgba(137,153,173,0.9)" font-size="12" font-weight="700">'
                    + value + '</text>';
        }).join("");
        var dots = points.map(function (point) {
            return '<circle class="trend-dot" cx="' + point.x.toFixed(2) + '" cy="' + point.y.toFixed(2)
                    + '" r="5"><title>' + point.score.toFixed(1) + " at " + point.label + '</title></circle>';
        }).join("");

        stage.innerHTML = '<svg viewBox="0 0 ' + width + ' ' + height + '" role="img" aria-label="Session score history">'
                + grid
                + '<path class="trend-area" d="' + area + '"></path>'
                + '<path class="trend-line" d="' + line + '"></path>'
                + dots
                + '</svg>';
    }

    page.querySelectorAll("[data-epoch]").forEach(function (node) {
        node.textContent = formatEpoch(node.getAttribute("data-epoch"));
    });

    page.querySelectorAll("[data-duration-label]").forEach(function (node) {
        node.textContent = formatDuration(node.getAttribute("data-duration-label"));
    });

    tabTriggers.forEach(function (trigger, index) {
        trigger.addEventListener("click", function () {
            setActiveTab(trigger.getAttribute("data-session-tab-trigger"));
        });

        trigger.addEventListener("keydown", function (event) {
            if (event.key !== "ArrowRight" && event.key !== "ArrowLeft" && event.key !== "Home" && event.key !== "End") {
                return;
            }

            event.preventDefault();

            var nextIndex = index;
            if (event.key === "ArrowRight") {
                nextIndex = (index + 1) % tabTriggers.length;
            } else if (event.key === "ArrowLeft") {
                nextIndex = (index - 1 + tabTriggers.length) % tabTriggers.length;
            } else if (event.key === "Home") {
                nextIndex = 0;
            } else if (event.key === "End") {
                nextIndex = tabTriggers.length - 1;
            }

            setActiveTab(tabTriggers[nextIndex].getAttribute("data-session-tab-trigger"));
            tabTriggers[nextIndex].focus();
        });
    });

    Array.prototype.slice.call(page.querySelectorAll("[data-session-score-chart]")).forEach(renderChart);
    setActiveTab("overview");
}());

/* ==========================================================
   PROFILE PAGE
   ========================================================== */
(function () {
    var page = document.querySelector("[data-profile-page]");

    if (!page) {
        return;
    }

    var TOAST_DURATION = 4000;
    var PASSWORD_API = "/api/admin/account/password";

    /* DOM refs ──────────────────────────────────────────── */
    var form = document.getElementById("profile-password-form");
    var currentPasswordInput = document.getElementById("profile-current-password");
    var newPasswordInput = document.getElementById("profile-new-password");
    var confirmPasswordInput = document.getElementById("profile-confirm-password");
    var submitBtn = document.getElementById("profile-submit-btn");
    var submitLabel = submitBtn ? submitBtn.querySelector(".profile-submit-label") : null;
    var submitSpinner = submitBtn ? submitBtn.querySelector(".profile-submit-spinner") : null;
    var toastSuccess = document.getElementById("profile-toast-success");
    var toastError = document.getElementById("profile-toast-error");
    var errorMessage = document.getElementById("profile-error-message");
    var validationBox = document.getElementById("profile-form-validation");
    var validationMessage = document.getElementById("profile-validation-message");
    var toastTimer = null;

    /* CSRF ──────────────────────────────────────────────── */
    function getCsrfToken() {
        var meta = document.querySelector('meta[name="_csrf"]');
        return meta ? meta.getAttribute("content") : null;
    }

    function getCsrfHeader() {
        var meta = document.querySelector('meta[name="_csrf_header"]');
        return meta ? meta.getAttribute("content") : "X-CSRF-TOKEN";
    }

    /* Timestamp formatting ──────────────────────────────── */
    function epochToDate(value) {
        var number = Number(value);
        if (!Number.isFinite(number) || number <= 0) {
            return null;
        }
        return new Date(number > 1000000000000 ? number : number * 1000);
    }

    function formatEpoch(value) {
        var date = epochToDate(value);
        if (!date) {
            return "Never";
        }
        return new Intl.DateTimeFormat(undefined, {
            year: "numeric",
            month: "short",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit"
        }).format(date);
    }

    page.querySelectorAll("[data-profile-epoch]").forEach(function (node) {
        var epoch = node.getAttribute("data-epoch");
        if (epoch && epoch !== "null" && epoch !== "") {
            node.textContent = formatEpoch(epoch);
        } else {
            node.textContent = "Never";
        }
    });

    /* Password visibility toggles ──────────────────────── */
    page.querySelectorAll("[data-toggle-password]").forEach(function (button) {
        button.addEventListener("click", function () {
            var targetId = button.getAttribute("data-toggle-password");
            var input = document.getElementById(targetId);
            if (!input) {
                return;
            }

            var isPassword = input.type === "password";
            input.type = isPassword ? "text" : "password";
            button.setAttribute("aria-label", isPassword ? "Hide password" : "Show password");
        });
    });

    /* Toast helpers ─────────────────────────────────────── */
    function showToast(el) {
        if (!el) {
            return;
        }

        if (toastTimer) {
            clearTimeout(toastTimer);
        }
        if (toastSuccess) {
            toastSuccess.hidden = true;
        }
        if (toastError) {
            toastError.hidden = true;
        }

        el.hidden = false;
        toastTimer = setTimeout(function () {
            el.hidden = true;
        }, TOAST_DURATION);
    }

    /* Validation ────────────────────────────────────────── */
    function clearValidation() {
        if (validationBox) {
            validationBox.hidden = true;
        }
        var inputs = [currentPasswordInput, newPasswordInput, confirmPasswordInput];
        inputs.forEach(function (input) {
            if (input) {
                input.classList.remove("is-invalid");
            }
        });
    }

    function showValidation(message, invalidInputs) {
        if (validationMessage) {
            validationMessage.textContent = message;
        }
        if (validationBox) {
            validationBox.hidden = false;
        }
        if (invalidInputs) {
            invalidInputs.forEach(function (input) {
                if (input) {
                    input.classList.add("is-invalid");
                }
            });
        }
    }

    function validate() {
        clearValidation();

        var currentPassword = currentPasswordInput ? currentPasswordInput.value.trim() : "";
        var newPassword = newPasswordInput ? newPasswordInput.value.trim() : "";
        var confirmPassword = confirmPasswordInput ? confirmPasswordInput.value.trim() : "";

        if (!currentPassword) {
            showValidation("Current password is required.", [currentPasswordInput]);
            return false;
        }

        if (!newPassword) {
            showValidation("New password is required.", [newPasswordInput]);
            return false;
        }

        if (!confirmPassword) {
            showValidation("Please confirm your new password.", [confirmPasswordInput]);
            return false;
        }

        if (newPassword !== confirmPassword) {
            showValidation("New password and confirmation do not match.", [newPasswordInput, confirmPasswordInput]);
            return false;
        }

        if (currentPassword === newPassword) {
            showValidation("New password must differ from the current password.", [newPasswordInput]);
            return false;
        }

        return true;
    }

    /* Loading state ─────────────────────────────────────── */
    function setLoading(isLoading) {
        if (submitBtn) {
            submitBtn.disabled = isLoading;
        }
        if (submitSpinner) {
            submitSpinner.hidden = !isLoading;
        }
    }

    /* Form submit ───────────────────────────────────────── */
    if (form) {
        form.addEventListener("submit", function (event) {
            event.preventDefault();

            if (!validate()) {
                return;
            }

            setLoading(true);

            var csrfToken = getCsrfToken();
            var csrfHeader = getCsrfHeader();
            var headers = {
                "Content-Type": "application/json",
                "Accept": "application/json"
            };
            if (csrfToken) {
                headers[csrfHeader] = csrfToken;
            }

            fetch(PASSWORD_API, {
                method: "POST",
                credentials: "same-origin",
                headers: headers,
                body: JSON.stringify({
                    currentPassword: currentPasswordInput.value.trim(),
                    newPassword: newPasswordInput.value.trim(),
                    confirmNewPassword: confirmPasswordInput.value.trim()
                })
            })
            .then(function (response) {
                if (response.ok) {
                    return response.json().then(function () {
                        return { ok: true };
                    });
                }
                return response.text().then(function (text) {
                    var message = "Failed to change password.";
                    try {
                        var parsed = JSON.parse(text);
                        if (parsed.message) {
                            message = parsed.message;
                        } else if (parsed.error) {
                            message = parsed.error;
                        }
                    } catch (e) {
                        if (text && text.length < 200) {
                            message = text;
                        }
                    }
                    return { ok: false, message: message };
                });
            })
            .then(function (result) {
                setLoading(false);

                if (result.ok) {
                    clearValidation();
                    if (currentPasswordInput) { currentPasswordInput.value = ""; }
                    if (newPasswordInput) { newPasswordInput.value = ""; }
                    if (confirmPasswordInput) { confirmPasswordInput.value = ""; }

                    /* Reset all to password type */
                    [currentPasswordInput, newPasswordInput, confirmPasswordInput].forEach(function (input) {
                        if (input) { input.type = "password"; }
                    });

                    showToast(toastSuccess);
                } else {
                    if (errorMessage) {
                        errorMessage.textContent = result.message;
                    }
                    showToast(toastError);
                }
            })
            .catch(function () {
                setLoading(false);
                if (errorMessage) {
                    errorMessage.textContent = "A network error occurred. Please try again.";
                }
                showToast(toastError);
            });
        });
    }
}());

/* ==========================================================
   SETTINGS PAGE
   ========================================================== */
(function () {
    var page = document.querySelector("[data-settings-page]");

    if (!page) {
        return;
    }

    var settingsApi = window.Phase2AdminSettings;
    var TOAST_DURATION = 3200;

    /* Default values ─────────────────────────────────────── */
    var DEFAULTS = settingsApi ? settingsApi.defaults : {
        theme: "dark",
        language: "en",
        tableDensity: "balanced",
        chartDensity: "balanced",
        driversDefaultView: "list",
        sidebarCollapsed: false
    };

    /* State ─────────────────────────────────────────────── */
    var saved = {};
    var pending = {};
    var isDirty = false;
    var toastTimer = null;

    /* DOM refs ──────────────────────────────────────────── */
    var dirtyBanner = document.getElementById("settings-dirty-banner");
    var toastSuccess = document.getElementById("settings-toast-success");
    var toastError = document.getElementById("settings-toast-error");
    var saveBtn = document.getElementById("settings-save-btn");
    var resetBtn = document.getElementById("settings-reset-btn");
    var saveBtnLabel = saveBtn ? saveBtn.querySelector(".save-btn-label") : null;
    var saveBtnSpinner = saveBtn ? saveBtn.querySelector(".save-btn-spinner") : null;

    var segControls = Array.prototype.slice.call(page.querySelectorAll("[data-setting]"))
        .filter(function (el) { return el.classList.contains("seg-control"); });

    var toggles = Array.prototype.slice.call(page.querySelectorAll("[data-setting][role='switch']"));
    var selects = Array.prototype.slice.call(page.querySelectorAll("select[data-setting]"));

    var subnavItems = Array.prototype.slice.call(
        document.querySelectorAll(".settings-subnav-item")
    );

    /* LocalStorage helpers ───────────────────────────────── */
    function loadSaved() {
        return settingsApi ? settingsApi.load() : {};
    }

    function persistSaved(data) {
        return settingsApi ? settingsApi.save(data) : false;
    }

    /* Dirty-state tracking ───────────────────────────────── */
    function checkDirty() {
        var dirty = false;
        Object.keys(pending).forEach(function (key) {
            var savedValue = Object.prototype.hasOwnProperty.call(saved, key)
                ? saved[key]
                : DEFAULTS[key];
            if (pending[key] !== savedValue) {
                dirty = true;
            }
        });
        isDirty = dirty;

        if (dirtyBanner) {
            dirtyBanner.hidden = !isDirty;
        }
        if (saveBtn) {
            saveBtn.disabled = !isDirty;
        }
    }

    /* UI renderers ───────────────────────────────────────── */
    function applySeg(control, value) {
        var buttons = Array.prototype.slice.call(control.querySelectorAll(".seg-btn"));
        buttons.forEach(function (btn) {
            var isActive = btn.getAttribute("data-value") === value;
            btn.classList.toggle("is-active", isActive);
            btn.setAttribute("aria-pressed", isActive ? "true" : "false");
        });
    }

    function applyToggle(toggle, value) {
        toggle.setAttribute("aria-checked", value ? "true" : "false");
    }

    function applySelect(select, value) {
        select.value = value;
    }

    function renderAll(values) {
        segControls.forEach(function (control) {
            var key = control.getAttribute("data-setting");
            var val = Object.prototype.hasOwnProperty.call(values, key) ? values[key] : DEFAULTS[key];
            applySeg(control, val);
        });

        toggles.forEach(function (toggle) {
            var key = toggle.getAttribute("data-setting");
            var val = Object.prototype.hasOwnProperty.call(values, key) ? values[key] : DEFAULTS[key];
            applyToggle(toggle, val);
        });

        selects.forEach(function (select) {
            var key = select.getAttribute("data-setting");
            var val = Object.prototype.hasOwnProperty.call(values, key) ? values[key] : DEFAULTS[key];
            applySelect(select, val);
        });
    }

    function currentValues() {
        var values = {};
        Object.keys(DEFAULTS).forEach(function (key) {
            values[key] = Object.prototype.hasOwnProperty.call(pending, key)
                ? pending[key]
                : (Object.prototype.hasOwnProperty.call(saved, key) ? saved[key] : DEFAULTS[key]);
        });
        return values;
    }

    function applyDraft() {
        if (settingsApi) {
            settingsApi.apply(currentValues());
        }
    }

    function syncDriversDefaultView(values) {
        var key = "phase2.admin.drivers";
        try {
            var driverState = JSON.parse(window.localStorage.getItem(key) || "{}");
            driverState.mode = values.driversDefaultView === "grid" ? "grid" : "list";
            window.localStorage.setItem(key, JSON.stringify(driverState));
        } catch (error) {
            return;
        }
    }

    /* Toast helpers ─────────────────────────────────────── */
    function showToast(el) {
        if (!el) { return; }

        if (toastTimer) {
            clearTimeout(toastTimer);
        }
        if (toastSuccess) { toastSuccess.hidden = true; }
        if (toastError) { toastError.hidden = true; }

        el.hidden = false;
        toastTimer = setTimeout(function () {
            el.hidden = true;
        }, TOAST_DURATION);
    }

    /* Save action ───────────────────────────────────────── */
    function doSave() {
        if (!isDirty || !saveBtn) { return; }

        /* Loading state */
        if (saveBtnLabel) { saveBtnLabel.hidden = false; }
        if (saveBtnSpinner) { saveBtnSpinner.hidden = false; }
        saveBtn.disabled = true;

        /* Browser storage is sync; keep the existing save spinner timing. */
        setTimeout(function () {
            var merged = {};
            Object.keys(DEFAULTS).forEach(function (key) {
                merged[key] = Object.prototype.hasOwnProperty.call(pending, key)
                    ? pending[key]
                    : (Object.prototype.hasOwnProperty.call(saved, key) ? saved[key] : DEFAULTS[key]);
            });

            var ok = persistSaved(merged);

            if (saveBtnSpinner) { saveBtnSpinner.hidden = true; }
            if (saveBtnLabel) { saveBtnLabel.hidden = false; }

            if (ok) {
                saved = merged;
                pending = {};
                isDirty = false;
                if (settingsApi) { settingsApi.apply(saved); }
                syncDriversDefaultView(saved);
                if (dirtyBanner) { dirtyBanner.hidden = true; }
                saveBtn.disabled = true;
                showToast(toastSuccess);
            } else {
                saveBtn.disabled = false;
                showToast(toastError);
            }
        }, 400);
    }

    /* Reset action ──────────────────────────────────────── */
    function doReset() {
        var hasChanges = false;
        Object.keys(DEFAULTS).forEach(function (key) {
            var current = Object.prototype.hasOwnProperty.call(pending, key)
                ? pending[key]
                : (Object.prototype.hasOwnProperty.call(saved, key) ? saved[key] : DEFAULTS[key]);
            if (current !== DEFAULTS[key]) { hasChanges = true; }
        });

        if (hasChanges) {
            if (!window.confirm("Reset all settings to their defaults?")) {
                return;
            }
        }

        var defaults = {};
        Object.keys(DEFAULTS).forEach(function (key) {
            defaults[key] = DEFAULTS[key];
        });

        var ok = persistSaved(defaults);
        if (ok) {
            saved = defaults;
            pending = {};
            renderAll(saved);
            if (settingsApi) { settingsApi.apply(saved); }
            syncDriversDefaultView(saved);
            showToast(toastSuccess);
        } else {
            showToast(toastError);
        }
        checkDirty();
    }

    /* Handle segmented control clicks ───────────────────── */
    segControls.forEach(function (control) {
        control.addEventListener("click", function (event) {
            var btn = event.target.closest(".seg-btn");
            if (!btn) { return; }
            var key = control.getAttribute("data-setting");
            var value = btn.getAttribute("data-value");
            pending[key] = value;
            applySeg(control, value);
            applyDraft();
            checkDirty();
        });
    });

    /* Handle toggle clicks ───────────────────────────────── */
    toggles.forEach(function (toggle) {
        toggle.addEventListener("click", function () {
            var key = toggle.getAttribute("data-setting");
            var currentValue = toggle.getAttribute("aria-checked") === "true";
            var newValue = !currentValue;
            pending[key] = newValue;
            applyToggle(toggle, newValue);
            applyDraft();
            checkDirty();
        });
    });

    /* Handle select changes ─────────────────────────────── */
    selects.forEach(function (select) {
        select.addEventListener("change", function () {
            var key = select.getAttribute("data-setting");
            pending[key] = select.value;
            applyDraft();
            checkDirty();
        });
    });

    /* Save / reset buttons ───────────────────────────────── */
    if (saveBtn) {
        saveBtn.addEventListener("click", doSave);
    }

    if (resetBtn) {
        resetBtn.addEventListener("click", doReset);
    }

    /* Sub-nav active state on scroll ────────────────────── */
    (function () {
        var sections = ["section-appearance", "section-display", "section-browsing"];

        function updateSubnav() {
            var scrollY = window.scrollY + 130;
            var active = sections[0];

            sections.forEach(function (id) {
                var el = document.getElementById(id);
                if (el && el.getBoundingClientRect().top + window.scrollY <= scrollY) {
                    active = id;
                }
            });

            subnavItems.forEach(function (item) {
                var href = item.getAttribute("href");
                var isActive = href === "#" + active;
                item.classList.toggle("is-active", isActive);
            });
        }

        window.addEventListener("scroll", updateSubnav, { passive: true });
        updateSubnav();
    }());

    /* Initialise ────────────────────────────────────────── */
    saved = loadSaved();
    var initial = {};
    Object.keys(DEFAULTS).forEach(function (key) {
        initial[key] = Object.prototype.hasOwnProperty.call(saved, key) ? saved[key] : DEFAULTS[key];
    });
    pending = {};
    renderAll(initial);
    checkDirty();
}());

(function () {
    var dashboard = document.querySelector(".workbench-dashboard");

    if (!dashboard) {
        return;
    }

    function humanize(value) {
        return String(value || "")
                .toLowerCase()
                .split("_")
                .filter(Boolean)
                .map(function (part) {
                    return part.charAt(0).toUpperCase() + part.slice(1);
                })
                .join(" ");
    }

    function clampScore(value) {
        if (Number.isNaN(value)) {
            return 0;
        }
        return Math.max(0, Math.min(100, value));
    }

    function formatDate(timestamp, includeTime) {
        var value = parseInt(timestamp, 10);
        if (!Number.isFinite(value) || value <= 0) {
            return "Unknown";
        }
        var date = new Date(value);
        if (Number.isNaN(date.getTime())) {
            return "Unknown";
        }
        var options = includeTime
                ? { year: "numeric", month: "short", day: "2-digit", hour: "2-digit", minute: "2-digit" }
                : { month: "short", day: "2-digit" };
        return new Intl.DateTimeFormat(undefined, options).format(date);
    }

    function pointPath(points) {
        return points.map(function (point, index) {
            return (index === 0 ? "M " : "L ") + point.x.toFixed(2) + " " + point.y.toFixed(2);
        }).join(" ");
    }

    function shortId(value) {
        var text = String(value || "");
        return text.length > 12 ? text.slice(0, 8) + "..." + text.slice(-4) : text;
    }

    function escapeSvg(value) {
        return String(value == null ? "" : value)
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;");
    }

    function toDayKey(timestamp) {
        var value = parseInt(timestamp, 10);
        if (!Number.isFinite(value) || value <= 0) {
            return "unknown";
        }
        var date = new Date(value);
        return date.getFullYear() + "-" + String(date.getMonth() + 1).padStart(2, "0") + "-" + String(date.getDate()).padStart(2, "0");
    }

    function aggregateScoreDays(data) {
        var groups = new Map();
        data.forEach(function (point) {
            var key = toDayKey(point.timestamp);
            var current = groups.get(key);
            if (!current) {
                current = {
                    timestamp: point.timestamp,
                    scoreTotal: 0,
                    count: 0
                };
                groups.set(key, current);
            }
            current.timestamp = Math.min(parseInt(current.timestamp, 10) || parseInt(point.timestamp, 10), parseInt(point.timestamp, 10));
            var count = Math.max(1, parseInt(point.count || "1", 10));
            current.scoreTotal += point.score * count;
            current.count += count;
        });
        return Array.from(groups.values()).map(function (group) {
            return {
                timestamp: group.timestamp,
                score: clampScore(group.scoreTotal / Math.max(group.count, 1)),
                count: group.count
            };
        });
    }

    function formatScoreTooltip(point, mode) {
        var parts = [
            formatDate(point.timestamp, mode === "line"),
            "Score: " + point.score.toFixed(1)
        ];
        if (mode === "bar") {
            parts.push(point.count + " sessions");
        } else {
            if (point.sessionId) {
                parts.push("Session: " + shortId(point.sessionId));
            }
            if (point.driverName || point.driverId) {
                parts.push("Driver: " + (point.driverName || point.driverId));
            }
        }
        return escapeSvg(parts.join("\n"));
    }

    function formatAverageTooltip(point) {
        return escapeSvg([
            formatDate(point.timestamp, false),
            "Average: " + point.score.toFixed(1),
            point.count + " sessions"
        ].join("\n"));
    }

    function scrollChartToEnd(card) {
        var scroll = card.querySelector("[data-chart-scroll]");
        if (!scroll) {
            return;
        }
        window.requestAnimationFrame(function () {
            scroll.scrollLeft = scroll.scrollWidth;
        });
    }

    function chartGeometry(width, height, margins) {
        return {
            chartOuterWidth: width,
            chartOuterHeight: height,
            marginTop: margins.top,
            marginRight: margins.right,
            marginBottom: margins.bottom,
            marginLeft: margins.left,
            plotWidth: width - margins.left - margins.right,
            plotHeight: height - margins.top - margins.bottom,
            plotTop: margins.top,
            plotBottom: height - margins.bottom,
            plotLeft: margins.left,
            plotRight: width - margins.right
        };
    }

    function valueToPlotY(value, yMin, yMax, geometry) {
        if (yMax === yMin) {
            return geometry.plotBottom;
        }
        return geometry.plotBottom - ((value - yMin) / (yMax - yMin)) * geometry.plotHeight;
    }

    function renderYAxis(axis, ticks, geometry) {
        if (!axis) {
            return;
        }
        axis.style.height = geometry.chartOuterHeight + "px";
        axis.innerHTML = ticks.map(function (value) {
            var y = valueToPlotY(value, ticks[ticks.length - 1], ticks[0], geometry);
            return '<span style="top:' + y.toFixed(2) + 'px">' + value + '</span>';
        }).join("");
    }

    function niceStep(max) {
        if (max <= 10) {
            return 5;
        }
        if (max <= 100) {
            return 25;
        }
        if (max <= 300) {
            return 50;
        }
        return 100;
    }

    function niceTicks(max) {
        var step = niceStep(max);
        var top = Math.max(step, Math.ceil(max / step) * step);
        if (top === max && max >= 50 && top / step < 4) {
            top += step;
        }
        var ticks = [];
        for (var value = top; value >= 0; value -= step) {
            ticks.push(value);
        }
        if (ticks[ticks.length - 1] !== 0) {
            ticks.push(0);
        }
        return ticks;
    }

    function shortCategoryLabel(label) {
        var labels = {
            HIGH_FATIGUE_EPISODE: "Fatigue",
            EMOTIONAL_DISTRESS_EPISODE: "Emotional Distress",
            OBSTACLE_NEAR_MISS: "Near Miss",
            HARSH_BRAKING: "Harsh Braking",
            LANE_DEPARTURE: "Lane Departure",
            REPEATED_HARSH_BRAKING: "Repeated Braking",
            FATIGUE_WITH_LANE_DEVIATION: "Fatigue + Lane",
            PROLONGED_LANE_DRIFT: "Lane Drift",
            MULTIPLE_EVENTS_SHORT_WINDOW: "Multi-Event Window"
        };
        return labels[label] || humanize(label);
    }

    function labelTspans(label, x, lineHeight) {
        var words = String(label || "").split(" ");
        if (words.length <= 1) {
            return escapeSvg(label);
        }
        var mid = Math.ceil(words.length / 2);
        return '<tspan x="' + x.toFixed(2) + '" dy="0">' + escapeSvg(words.slice(0, mid).join(" ")) + '</tspan>'
                + '<tspan x="' + x.toFixed(2) + '" dy="' + lineHeight + '">' + escapeSvg(words.slice(mid).join(" ")) + '</tspan>';
    }

    function getScorePoints(card) {
        var nodes = Array.prototype.slice.call(card.querySelectorAll("[data-score-point]"));
        if (nodes.length) {
            var pointData = nodes.map(function (node) {
                return {
                    timestamp: node.getAttribute("data-timestamp"),
                    score: clampScore(parseFloat(node.getAttribute("data-score") || "0")),
                    count: node.getAttribute("data-count") || "1",
                    sessionId: node.getAttribute("data-session-id") || "",
                    driverId: node.getAttribute("data-driver-id") || "",
                    driverName: node.getAttribute("data-driver-name") || ""
                };
            });
            card.setAttribute("data-score-points-cache", JSON.stringify(pointData));
            return pointData;
        }

        try {
            return JSON.parse(card.getAttribute("data-score-points-cache") || "[]");
        } catch (error) {
            return [];
        }
    }

    function renderScoreChart(card) {
        var stage = card.querySelector("[data-chart-stage]");
        var axis = card.querySelector(".score-chart .chart-y-axis");
        var modeButton = card.querySelector(".chart-toggle-button.is-active");
        var mode = modeButton ? modeButton.getAttribute("data-chart-mode") : "bar";
        var sourceData = getScorePoints(card);
        var data = mode === "bar" ? aggregateScoreDays(sourceData) : sourceData.slice();
        data.sort(function (a, b) {
            return (parseInt(a.timestamp, 10) || 0) - (parseInt(b.timestamp, 10) || 0);
        });

        if (!stage || !data.length) {
            return;
        }

        var dayCount = new Set(data.map(function (point) { return toDayKey(point.timestamp); })).size;
        var width = Math.max(
                card.classList.contains("is-expanded") ? 1040 : 620,
                mode === "line" ? Math.max(data.length * 34, dayCount * 92) : data.length * 88
        );
        var height = card.classList.contains("is-expanded") ? 470 : 270;
        var geometry = chartGeometry(width, height, { top: 22, right: 22, bottom: 48, left: 10 });
        var scoreTicks = [100, 75, 50, 25, 0];
        var step = geometry.plotWidth / Math.max(data.length, 1);
        var barWidth = Math.min(42, Math.max(20, step * 0.56));
        var parsedTimestamps = data.map(function (point) {
            return parseInt(point.timestamp, 10) || 0;
        }).filter(function (timestamp) {
            return timestamp > 0;
        });
        var minTimestamp = parsedTimestamps.length ? Math.min.apply(null, parsedTimestamps) : 0;
        var maxTimestamp = parsedTimestamps.length ? Math.max.apply(null, parsedTimestamps) : 0;
        var timestampCounts = new Map();
        var timestampSeen = new Map();
        if (mode === "line") {
            data.forEach(function (pointData) {
                var key = String(parseInt(pointData.timestamp, 10) || 0);
                timestampCounts.set(key, (timestampCounts.get(key) || 0) + 1);
            });
        }
        function xForTimestamp(timestamp, fallbackIndex) {
            var parsed = parseInt(timestamp, 10) || 0;
            if (mode === "line" && minTimestamp > 0 && maxTimestamp > minTimestamp && parsed > 0) {
                return geometry.plotLeft + ((parsed - minTimestamp) / (maxTimestamp - minTimestamp)) * geometry.plotWidth;
            }
            return geometry.plotLeft + step * fallbackIndex + step / 2;
        }
        var points = data.map(function (pointData, index) {
            var score = pointData.score;
            var x = xForTimestamp(pointData.timestamp, index);
            if (mode === "line") {
                var key = String(parseInt(pointData.timestamp, 10) || 0);
                var countAtTimestamp = timestampCounts.get(key) || 1;
                var seenAtTimestamp = timestampSeen.get(key) || 0;
                timestampSeen.set(key, seenAtTimestamp + 1);
                x += (seenAtTimestamp - (countAtTimestamp - 1) / 2) * Math.min(10, Math.max(4, step * 0.28));
                x = Math.max(geometry.plotLeft, Math.min(geometry.plotRight, x));
            }
            var y = valueToPlotY(score, 0, 100, geometry);
            return {
                x: x,
                y: y,
                score: score,
                count: pointData.count,
                timestamp: pointData.timestamp,
                sessionId: pointData.sessionId,
                driverId: pointData.driverId,
                driverName: pointData.driverName,
                label: formatDate(pointData.timestamp, false)
            };
        });
        var grid = scoreTicks.map(function (value) {
            var y = valueToPlotY(value, 0, 100, geometry);
            return '<line class="score-grid-line" x1="' + geometry.plotLeft + '" y1="' + y.toFixed(2) + '" x2="' + geometry.plotRight + '" y2="' + y.toFixed(2) + '"></line>';
        }).join("");
        var labels = points.map(function (point, index) {
            var interval = Math.max(1, Math.ceil(data.length / Math.max(6, Math.floor(width / 92))));
            if (data.length > 8 && index % interval !== 0 && index !== data.length - 1) {
                return "";
            }
            return '<text class="score-x-label" x="' + point.x.toFixed(2) + '" y="' + (height - 14) + '" text-anchor="middle">' + escapeSvg(point.label) + '</text>';
        }).join("");
        var marks = "";

        if (mode === "line") {
            var averagePoints = aggregateScoreDays(data).map(function (pointData, index) {
                var score = pointData.score;
                var x = xForTimestamp(pointData.timestamp, index);
                return {
                    x: x,
                    y: valueToPlotY(score, 0, 100, geometry),
                    score: score,
                    count: pointData.count,
                    timestamp: pointData.timestamp
                };
            });
            if (averagePoints.length >= 2) {
                marks += '<path class="score-average-line" d="' + pointPath(averagePoints) + '"></path>';
                marks += averagePoints.map(function (point) {
                    return '<circle class="score-average-hit" cx="' + point.x.toFixed(2) + '" cy="' + point.y.toFixed(2) + '" r="7">'
                            + '<title>' + formatAverageTooltip(point) + '</title></circle>';
                }).join("");
            } else if (averagePoints.length === 1) {
                marks += '<line class="score-average-line" x1="' + geometry.plotLeft + '" y1="' + averagePoints[0].y.toFixed(2)
                        + '" x2="' + geometry.plotRight + '" y2="' + averagePoints[0].y.toFixed(2) + '">'
                        + '<title>' + formatAverageTooltip(averagePoints[0]) + '</title></line>';
            }
            var labelsByDay = new Map();
            points.forEach(function (point) {
                var key = toDayKey(point.timestamp);
                if (!labelsByDay.has(key)) {
                    labelsByDay.set(key, point);
                }
            });
            labels = Array.from(labelsByDay.values()).map(function (point, index, dayPoints) {
                var interval = Math.max(1, Math.ceil(dayPoints.length / Math.max(6, Math.floor(width / 92))));
                if (dayPoints.length > 8 && index % interval !== 0 && index !== dayPoints.length - 1) {
                    return "";
                }
                return '<text class="score-x-label" x="' + point.x.toFixed(2) + '" y="' + (height - 14) + '" text-anchor="middle">' + escapeSvg(formatDate(point.timestamp, false)) + '</text>';
            }).join("");
            marks += points.map(function (point) {
                return '<circle class="score-dot" cx="' + point.x.toFixed(2) + '" cy="' + point.y.toFixed(2) + '" r="5">'
                        + '<title>' + formatScoreTooltip(point, mode) + '</title></circle>';
            }).join("");
        } else {
            marks += points.map(function (point) {
                var barHeight = Math.max(2, geometry.plotBottom - point.y);
                return '<rect class="score-bar" x="' + (point.x - barWidth / 2).toFixed(2) + '" y="' + point.y.toFixed(2)
                        + '" width="' + barWidth.toFixed(2) + '" height="' + barHeight.toFixed(2) + '" rx="3">'
                        + '<title>' + formatScoreTooltip(point, mode) + '</title></rect>';
            }).join("");
        }

        renderYAxis(axis, scoreTicks, geometry);
        stage.style.minWidth = width + "px";
        stage.style.height = height + "px";
        stage.innerHTML = '<svg class="score-chart-svg" width="' + width + '" height="' + height + '" viewBox="0 0 ' + width + ' ' + height + '" role="img" aria-label="Average final score by submission date">'
                + grid + marks + labels + '</svg>';
        scrollChartToEnd(card);
    }

    function renderCategoryBarChart(card) {
        var stage = card.querySelector("[data-category-chart-stage]");
        var axis = card.querySelector(".category-chart .chart-y-axis");
        var nodes = Array.prototype.slice.call(card.querySelectorAll("[data-category-point]"));

        if (!stage || !nodes.length) {
            return;
        }

        var values = nodes.map(function (node) {
            return {
                label: shortCategoryLabel(node.getAttribute("data-label")),
                fullLabel: humanize(node.getAttribute("data-label")),
                count: Math.max(0, parseInt(node.getAttribute("data-count") || "0", 10))
            };
        }).filter(function (point) {
            return point.count > 0;
        });

        if (!values.length) {
            return;
        }

        var max = Math.max.apply(null, values.map(function (point) { return point.count; }));
        var ticks = niceTicks(max);
        var axisMax = ticks[0];
        var width = Math.max(card.classList.contains("is-expanded") ? 900 : 560, values.length * 132);
        var height = card.classList.contains("is-expanded") ? 430 : 250;
        var geometry = chartGeometry(width, height, { top: 20, right: 18, bottom: 72, left: 10 });
        var step = geometry.plotWidth / values.length;
        var barWidth = Math.min(58, Math.max(28, step * 0.54));
        var warning = card.querySelector(".warning-chart") !== null;
        var bars = values.map(function (point, index) {
            var x = geometry.plotLeft + step * index + (step - barWidth) / 2;
            var y = valueToPlotY(point.count, 0, axisMax, geometry);
            var barHeight = Math.max(3, geometry.plotBottom - y);
            var labelX = x + barWidth / 2;
            return '<rect class="category-bar' + (warning ? " warning" : "") + '" x="' + x.toFixed(2) + '" y="' + y.toFixed(2)
                    + '" width="' + barWidth.toFixed(2) + '" height="' + barHeight.toFixed(2) + '" rx="3">'
                    + '<title>' + escapeSvg(point.fullLabel + ": " + point.count) + '</title></rect>'
                    + '<text class="category-value-label" x="' + labelX.toFixed(2) + '" y="' + (y - 7).toFixed(2) + '" text-anchor="middle">' + point.count + '</text>'
                    + '<text class="category-x-label" x="' + labelX.toFixed(2) + '" y="' + (height - 44) + '" text-anchor="middle">'
                    + labelTspans(point.label, labelX, 12) + '</text>';
        }).join("");
        var grid = ticks.map(function (value) {
            var y = valueToPlotY(value, 0, axisMax, geometry);
            return '<line class="score-grid-line" x1="' + geometry.plotLeft + '" y1="' + y.toFixed(2) + '" x2="' + geometry.plotRight + '" y2="' + y.toFixed(2) + '"></line>';
        }).join("");

        renderYAxis(axis, ticks, geometry);
        stage.style.minWidth = width + "px";
        stage.style.height = height + "px";
        stage.innerHTML = '<svg class="category-chart-svg" width="' + width + '" height="' + height + '" viewBox="0 0 ' + width + ' ' + height + '" role="img" aria-label="' + stage.getAttribute("aria-label") + '">'
                + grid + bars + '</svg>';
    }

    Array.prototype.slice.call(dashboard.querySelectorAll("[data-humanize]")).forEach(function (node) {
        node.textContent = humanize(node.textContent);
    });

    Array.prototype.slice.call(dashboard.querySelectorAll("[data-epoch-time]")).forEach(function (node) {
        var value = node.getAttribute("datetime") || node.textContent;
        node.textContent = formatDate(value, true);
    });

    Array.prototype.slice.call(dashboard.querySelectorAll("[data-dashboard-score-chart]")).forEach(function (chart) {
        var card = chart.closest("[data-expandable-chart]");
        if (card) {
            renderScoreChart(card);
        }
    });

    Array.prototype.slice.call(dashboard.querySelectorAll("[data-dashboard-category-chart]")).forEach(function (chart) {
        var card = chart.closest("[data-expandable-chart]");
        if (card) {
            renderCategoryBarChart(card);
        }
    });

    function renderCardChart(card) {
        renderScoreChart(card);
        renderCategoryBarChart(card);
    }

    function setExpanded(targetCard, shouldExpand) {
        Array.prototype.slice.call(dashboard.querySelectorAll("[data-expandable-chart].is-expanded")).forEach(function (card) {
            if (card !== targetCard) {
                card.classList.remove("is-expanded");
                renderCardChart(card);
            }
        });
        targetCard.classList.toggle("is-expanded", shouldExpand);
        document.body.classList.toggle("has-expanded-chart", shouldExpand);
        renderCardChart(targetCard);
    }

    dashboard.addEventListener("click", function (event) {
        var modeButton = event.target.closest("[data-chart-mode]");
        var closeButton = event.target.closest("[data-chart-close]");

        if (modeButton) {
            var card = modeButton.closest("[data-expandable-chart]");
            Array.prototype.slice.call(card.querySelectorAll("[data-chart-mode]")).forEach(function (button) {
                var active = button === modeButton;
                button.classList.toggle("is-active", active);
                button.setAttribute("aria-pressed", active ? "true" : "false");
            });
            renderScoreChart(card);
            return;
        }

        if (closeButton) {
            setExpanded(closeButton.closest("[data-expandable-chart]"), false);
            return;
        }

        var targetCard = event.target.closest("[data-expandable-chart]");
        if (targetCard && !event.target.closest("a, button")) {
            setExpanded(targetCard, true);
        }
    });

    document.addEventListener("keydown", function (event) {
        if (event.key !== "Escape") {
            return;
        }
        Array.prototype.slice.call(dashboard.querySelectorAll("[data-expandable-chart].is-expanded")).forEach(function (card) {
            card.classList.remove("is-expanded");
            renderCardChart(card);
        });
        document.body.classList.remove("has-expanded-chart");
    });
}());
