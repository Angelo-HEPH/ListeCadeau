<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
      rel="stylesheet">

<%
    Integer userId = (Integer) session.getAttribute("userId");

    Integer unread = (Integer) request.getAttribute("unreadNotifCount");
    if (unread == null) unread = 0;
%>

<nav class="navbar navbar-expand-lg bg-white shadow-sm mb-4">
    <div class="container">

        <a class="navbar-brand fw-bold" href="<%= request.getContextPath() %>/home">
            🎁 Gift App
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarContent">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarContent">

            <div class="ms-auto d-flex align-items-center gap-2">

                <% if (userId == null) { %>

                    <a href="<%= request.getContextPath() %>/login"
                       class="btn btn-outline-primary btn-sm">
                        Connexion
                    </a>

                    <a href="<%= request.getContextPath() %>/createAccount"
                       class="btn btn-primary btn-sm">
                        Créer un compte
                    </a>

                <% } else { %>

                    <a href="<%= request.getContextPath() %>/notifications"
                       class="btn btn-outline-secondary btn-sm position-relative">
                        🔔
                        <% if (unread > 0) { %>
                            <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                                <%= unread %>
                            </span>
                        <% } %>
                    </a>

                    <a href="<%= request.getContextPath() %>/profile"
                       class="btn btn-outline-secondary btn-sm">
                        👤Profil
                    </a>

                    <a href="<%= request.getContextPath() %>/logout"
                       class="btn btn-danger btn-sm">
                        Déconnexion
                    </a>

                <% } %>

            </div>

        </div>
    </div>
</nav>
