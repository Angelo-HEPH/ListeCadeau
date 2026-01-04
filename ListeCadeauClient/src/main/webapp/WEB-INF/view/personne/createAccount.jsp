<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Créer un compte</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

    <h2 class="mb-4">Créer un compte</h2>

    <%
        String error = (String) request.getAttribute("error");
        String message = (String) request.getAttribute("message");

        String name = (String) request.getAttribute("name");
        String firstName = (String) request.getAttribute("firstName");
        String age = (String) request.getAttribute("age");
        String street = (String) request.getAttribute("street");
        String city = (String) request.getAttribute("city");
        String streetNumber = (String) request.getAttribute("streetNumber");
        String postalCode = (String) request.getAttribute("postalCode");
        String email = (String) request.getAttribute("email");
    %>

    <% if (error != null && !error.trim().isEmpty()) { %>
        <p class="text-danger fw-bold"><%= error %></p>
    <% } %>

    <% if (message != null && !message.trim().isEmpty()) { %>
        <p class="text-success fw-bold"><%= message %></p>
    <% } %>

    <form action="createAccount" method="post" class="row g-3">

        <div class="col-md-6">
            <label class="form-label">Nom :</label>
            <input type="text" class="form-control" name="name" required
                   value="<%= (name != null ? name : "") %>">
        </div>

        <div class="col-md-6">
            <label class="form-label">Prénom :</label>
            <input type="text" class="form-control" name="firstName" required
                   value="<%= (firstName != null ? firstName : "") %>">
        </div>

        <div class="col-md-4">
            <label class="form-label">Âge :</label>
            <input type="number" class="form-control" name="age" required
                   value="<%= (age != null ? age : "") %>">
        </div>

        <div class="col-md-8">
            <label class="form-label">Rue :</label>
            <input type="text" class="form-control" name="street" required
                   value="<%= (street != null ? street : "") %>">
        </div>

        <div class="col-md-6">
            <label class="form-label">Ville :</label>
            <input type="text" class="form-control" name="city" required
                   value="<%= (city != null ? city : "") %>">
        </div>

        <div class="col-md-3">
            <label class="form-label">Numéro :</label>
            <input type="text" class="form-control" name="streetNumber" required
                   value="<%= (streetNumber != null ? streetNumber : "") %>">
        </div>

        <div class="col-md-3">
            <label class="form-label">Code postal :</label>
            <input type="number" class="form-control" name="postalCode" required
                   value="<%= (postalCode != null ? postalCode : "") %>">
        </div>

        <div class="col-md-6">
            <label class="form-label">Email :</label>
            <input type="email" class="form-control" name="email" required
                   value="<%= (email != null ? email : "") %>">
        </div>

        <div class="col-md-6">
            <label class="form-label">Mot de passe :</label>
            <input type="password" class="form-control" name="password" required>
        </div>

        <div class="col-12 mt-3">
            <button type="submit" class="btn btn-primary">Créer le compte</button>
            <a href="login" class="btn btn-secondary ms-2">Retour</a>
        </div>

    </form>

</div>

</body>
</html>
