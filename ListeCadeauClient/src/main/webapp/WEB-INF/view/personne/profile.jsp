<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="be.couderiannello.models.Personne" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Mon profil</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

<%
    Personne user = (Personne) request.getAttribute("user");
%>

<h2 class="mb-4">👤 Mon profil</h2>

<div class="card shadow-sm p-4" style="max-width:600px;">

    <h5 class="mb-3">Informations personnelles</h5>

    <p><b>Prénom :</b> <%= user.getFirstName() %></p>
    <p><b>Nom :</b> <%= user.getName() %></p>
    <p><b>Email :</b> <%= user.getEmail() %></p>
    <p><b>Âge :</b> <%= user.getAge() %> ans</p>

    <hr>

    <h5 class="mb-3">Adresse</h5>

    <p>
        <%= user.getStreet() %> <%= user.getStreetNumber() %><br>
        <%= user.getPostalCode() %> <%= user.getCity() %>
    </p>

    <a href="<%= request.getContextPath() %>/profile/edit"
       class="btn btn-warning mt-3">
        ✏️ Modifier mon profil
    </a>

</div>

</div>

</body>
</html>
