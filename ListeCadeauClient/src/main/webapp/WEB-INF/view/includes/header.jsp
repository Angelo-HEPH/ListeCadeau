<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" 
      rel="stylesheet">

<%

	Integer userId = (Integer) session.getAttribute("userId");
	String userName = (String) session.getAttribute("firstName");
%>

<nav class="navbar navbar-light bg-light px-3 mb-4">

    <a class="navbar-brand" href="<%= request.getContextPath() %>/home">🎁 Gift App</a>

    <div class="d-flex align-items-center gap-2">

        <% if (userId == null) { %>

            <a href="<%= request.getContextPath() %>/login" 
               class="btn btn-outline-primary btn-sm">Connexion</a>

            <a href="<%= request.getContextPath() %>/createAccount" 
               class="btn btn-primary btn-sm">Créer un compte</a>

        <% } else { %>

            <span class="me-2">Bonjour, <b><%= userName %></b></span>
            
            <a href="<%= request.getContextPath() %>/profile"
				   class="btn btn-outline-secondary btn-sm">
				    👤 Mon profil
		    </a>
            

            <a href="<%= request.getContextPath() %>/logout" 
               class="btn btn-danger btn-sm">Déconnexion</a>

        <% } %>

    </div>
</nav>
