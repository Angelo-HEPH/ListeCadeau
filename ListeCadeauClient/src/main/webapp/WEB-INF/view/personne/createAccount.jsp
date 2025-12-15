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

    <c:if test="${not empty error}">
        <p class="text-danger fw-bold">${error}</p>
    </c:if>

    <c:if test="${not empty message}">
        <p class="text-success fw-bold">${message}</p>
    </c:if>

    <form action="createAccount" method="post" class="row g-3">

        <div class="col-md-6">
            <label class="form-label">Nom :</label>
            <input type="text" class="form-control" name="name" required>
        </div>

        <div class="col-md-6">
            <label class="form-label">Prénom :</label>
            <input type="text" class="form-control" name="firstName" required>
        </div>

        <div class="col-md-4">
            <label class="form-label">Âge :</label>
            <input type="number" class="form-control" name="age" required>
        </div>

        <div class="col-md-8">
            <label class="form-label">Rue :</label>
            <input type="text" class="form-control" name="street" required>
        </div>

        <div class="col-md-6">
            <label class="form-label">Ville :</label>
            <input type="text" class="form-control" name="city" required>
        </div>

        <div class="col-md-3">
            <label class="form-label">Numéro :</label>
            <input type="text" class="form-control" name="streetNumber" required>
        </div>

        <div class="col-md-3">
            <label class="form-label">Code postal :</label>
            <input type="number" class="form-control" name="postalCode" required>
        </div>

        <div class="col-md-6">
            <label class="form-label">Email :</label>
            <input type="email" class="form-control" name="email" required>
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
