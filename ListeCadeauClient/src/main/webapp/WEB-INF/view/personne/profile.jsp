<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="be.couderiannello.models.Personne" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Mon profil</title>
</head>
<body class="bg-light">

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container my-5">

<%
    Personne user = (Personne) request.getAttribute("user");
    String error = (String) request.getAttribute("error");
%>

<h2 class="text-center mb-4 fw-bold">👤 Mon profil</h2>

<% if (error != null && !error.isBlank()) { %>
    <div class="alert alert-danger text-center fw-bold mb-4">
        <%= error %>
    </div>
<% } %>

<% if (user != null) { %>

<div class="bg-white shadow-lg rounded-4 p-5 mx-auto" style="max-width:600px;">

    <h4 class="mb-3 fw-bold">Informations personnelles</h4>

    <p>🧑 <b>Prénom :</b> <%= user.getFirstName() %></p>
    <p>🧑 <b>Nom :</b> <%= user.getName() %></p>
    <p>✉️ <b>Email :</b> <%= user.getEmail() %></p>
    <p>🎂 <b>Âge :</b> <%= user.getAge() %> ans</p>

    <hr class="my-4">

    <h4 class="mb-3 fw-bold">Adresse</h4>

    <p>
        <%= user.getStreet() %> <%= user.getStreetNumber() %><br>
        <%= user.getPostalCode() %> <%= user.getCity() %>
    </p>

    <div class="d-flex justify-content-between mt-4">
        <a href="<%= request.getContextPath() %>/profile/edit"
           class="btn btn-warning fw-bold btn-lg">
            ✏️ Modifier mon profil
        </a>

        <a href="<%= request.getContextPath() %>/home"
           class="btn btn-secondary fw-bold btn-lg">
            ← Retour
        </a>
    </div>

</div>

<% } %>

</div>
</body>
</html>
