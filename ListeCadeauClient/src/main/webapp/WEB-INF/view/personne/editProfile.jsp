<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="be.couderiannello.models.Personne" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Modifier le profil</title>
</head>
<body class="bg-light">

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container my-5">

<%
    Personne user = (Personne) request.getAttribute("user");
    String error = (String) request.getAttribute("error");
%>

<h2 class="text-center mb-4 fw-bold">✏️ Modifier mon profil</h2>

<% if (error != null && !error.trim().isEmpty()) { %>
    <div class="alert alert-danger text-center fw-bold mb-4">
        <%= error %>
    </div>
<% } %>

<% if (user != null) { %>

<div class="bg-white shadow-lg rounded-4 p-5 mx-auto" style="max-width:600px;">

    <form method="post"
          action="<%= request.getContextPath() %>/profile/edit">

        <div class="mb-3">
            <label class="form-label fw-semibold">🧑 Prénom</label>
            <input type="text" name="firstName" class="form-control form-control-lg"
                   value="<%= user.getFirstName() %>" required>
        </div>

        <div class="mb-3">
            <label class="form-label fw-semibold">🧑 Nom</label>
            <input type="text" name="name" class="form-control form-control-lg"
                   value="<%= user.getName() %>" required>
        </div>

        <div class="mb-3">
            <label class="form-label fw-semibold">🎂 Âge</label>
            <input type="number" name="age" class="form-control form-control-lg"
                   value="<%= user.getAge() %>" required>
        </div>

        <div class="mb-3">
            <label class="form-label fw-semibold">🏠 Rue</label>
            <input type="text" name="street" class="form-control form-control-lg"
                   value="<%= user.getStreet() %>" required>
        </div>

        <div class="mb-3">
            <label class="form-label fw-semibold">🏷️ Numéro</label>
            <input type="text" name="streetNumber" class="form-control form-control-lg"
                   value="<%= user.getStreetNumber() %>" required>
        </div>

        <div class="mb-3">
            <label class="form-label fw-semibold">🏙️ Ville</label>
            <input type="text" name="city" class="form-control form-control-lg"
                   value="<%= user.getCity() %>" required>
        </div>

        <div class="mb-3">
            <label class="form-label fw-semibold">📮 Code postal</label>
            <input type="number" name="postalCode" class="form-control form-control-lg"
                   value="<%= user.getPostalCode() %>" required>
        </div>

        <div class="mb-3">
            <label class="form-label fw-semibold">✉️ Email</label>
            <input type="email" name="email" class="form-control form-control-lg"
                   value="<%= user.getEmail() %>" required>
        </div>

        <div class="d-flex justify-content-between mt-4">
            <button type="submit" class="btn btn-primary fw-bold btn-lg">
                💾 Enregistrer
            </button>

            <a href="<%= request.getContextPath() %>/profile"
               class="btn btn-secondary fw-bold btn-lg">
                ❌ Annuler
            </a>
        </div>

    </form>

</div>

<% } else { %>

    <div class="alert alert-warning text-center">
        Impossible de charger le profil.
    </div>
    <div class="text-center mt-3">
        <a href="<%= request.getContextPath() %>/profile" class="btn btn-secondary btn-lg">
            ← Retour
        </a>
    </div>

<% } %>

</div>
</body>
</html>
