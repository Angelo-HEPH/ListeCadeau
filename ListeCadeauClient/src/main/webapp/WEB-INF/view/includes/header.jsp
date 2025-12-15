<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" 
      rel="stylesheet">

<%
    be.couderiannello.models.Personne user =
        (be.couderiannello.models.Personne) session.getAttribute("user");
%>

<nav class="navbar navbar-light bg-light px-3 mb-4">

    <a class="navbar-brand" href="<%= request.getContextPath() %>/home">🎁 Gift App</a>

    <div class="d-flex align-items-center gap-2">

        <% if (user == null) { %>

            <a href="<%= request.getContextPath() %>/login" 
               class="btn btn-outline-primary btn-sm">Connexion</a>

            <a href="<%= request.getContextPath() %>/createAccount" 
               class="btn btn-primary btn-sm">Créer un compte</a>

        <% } else { %>

            <span class="me-2">Bonjour, <b><%= user.getFirstName() %></b></span>

            <a href="<%= request.getContextPath() %>/logout" 
               class="btn btn-danger btn-sm">Déconnexion</a>

        <% } %>

    </div>
</nav>
