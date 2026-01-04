<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="java.util.*,
                 java.time.format.DateTimeFormatter,
                 be.couderiannello.models.Notification" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Mes notifications</title>
</head>

<body class="bg-light">

    <jsp:include page="/WEB-INF/view/includes/header.jsp" />

    <div class="container my-5">

        <%
            List<Notification> notifications =
                (List<Notification>) request.getAttribute("notifications");

            DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

            boolean hasUnread = false;

            if (notifications != null) {
                for (Notification n : notifications) {
                    if (!n.isRead()) {
                        hasUnread = true;
                        break;
                    }
                }
            }
        %>

        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="fw-bold mb-0">🔔 Mes notifications</h2>

            <% if (hasUnread) { %>
                <form method="post" action="<%= request.getContextPath() %>/notifications">
                    <button type="submit" class="btn btn-sm btn-primary fw-bold">
                        Tout marquer comme lu
                    </button>
                </form>
            <% } %>
        </div>

        <% if (notifications == null || notifications.isEmpty()) { %>
            <div class="alert alert-info text-center">
                Vous n'avez aucune notification.
            </div>
        <% } else { %>

            <div class="row g-3">
                <%
                    for (Notification n : notifications) {
                        String bgClass = n.isRead() ? "bg-white" : "bg-warning bg-opacity-25";
                %>

                        <div class="col-12">
                            <div class="card <%= bgClass %> shadow-sm p-3 d-flex justify-content-between align-items-start">
                                <div>
                                    <div class="fw-bold mb-1">
                                        <%= n.getMessage() %>
                                    </div>
                                    <small class="text-muted">
                                        📅 <%= n.getSendDate().format(formatter) %>
                                    </small>
                                </div>

                                <% if (!n.isRead()) { %>
                                    <span class="badge bg-danger rounded-pill ms-3">
                                        Nouveau
                                    </span>
                                <% } %>
                            </div>
                        </div>

                <%
                    }
                %>
            </div>

        <% } %>

        <div class="mt-4 text-center">
            <a href="<%= request.getContextPath() %>/home" class="btn btn-secondary fw-bold">
                ← Retour au menu principal
            </a>
        </div>

    </div>
</body>
</html>
