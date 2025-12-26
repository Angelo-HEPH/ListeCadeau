<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="be.couderiannello.models.Personne" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Modifier le profil</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

<%
    Personne user = (Personne) request.getAttribute("user");
%>

<h2 class="mb-4">✏️ Modifier mon profil</h2>

<form method="post"
      action="<%= request.getContextPath() %>/profile/edit"
      class="card p-4 shadow-sm"
      style="max-width:600px;">

    <div class="mb-3">
        <label class="form-label">Prénom</label>
        <input type="text" name="firstName" class="form-control"
               value="<%= user.getFirstName() %>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Nom</label>
        <input type="text" name="name" class="form-control"
               value="<%= user.getName() %>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Âge</label>
        <input type="number" name="age" class="form-control"
               value="<%= user.getAge() %>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Rue</label>
        <input type="text" name="street" class="form-control"
               value="<%= user.getStreet() %>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Numéro</label>
        <input type="text" name="streetNumber" class="form-control"
               value="<%= user.getStreetNumber() %>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Ville</label>
        <input type="text" name="city" class="form-control"
               value="<%= user.getCity() %>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Code postal</label>
        <input type="number" name="postalCode" class="form-control"
               value="<%= user.getPostalCode() %>" required>
    </div>

    <div class="mb-3">
        <label class="form-label">Email</label>
        <input type="email" name="email" class="form-control"
               value="<%= user.getEmail() %>" required>
    </div>

    <button type="submit" class="btn btn-success">
        Enregistrer
    </button>

    <a href="<%= request.getContextPath() %>/profile"
       class="btn btn-secondary ms-2">
        Annuler
    </a>

</form>

</div>
</body>
</html>
