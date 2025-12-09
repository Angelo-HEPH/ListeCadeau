<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Créer un compte</title>
</head>
<body>

    <h2>Créer un compte</h2>

	<form action="createAccount" method="post">

        <label>Nom :</label><br>
        <input type="text" name="name" required><br><br>

        <label>Prénom :</label><br>
        <input type="text" name="firstName" required><br><br>

        <label>Âge :</label><br>
        <input type="number" name="age" required><br><br>

        <label>Rue :</label><br>
        <input type="text" name="street" required><br><br>

        <label>Ville :</label><br>
        <input type="text" name="city" required><br><br>

        <label>Numéro :</label><br>
        <input type="text" name="streetNumber" required><br><br>

        <label>Code postal :</label><br>
        <input type="number" name="postalCode" required><br><br>

        <label>Email :</label><br>
        <input type="email" name="email" required><br><br>

        <label>Mot de passe :</label><br>
        <input type="password" name="password" required><br><br>

        <button type="submit">Créer le compte</button>
    </form>

</body>
</html>
