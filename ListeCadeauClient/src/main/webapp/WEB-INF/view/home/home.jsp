<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Home</title>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />
</head>
<body>

<div class="container mt-5">

    <h1 class="mb-4">Bienvenue <span class="text-primary">${user.firstName}</span> !</h1>

    <h2 class="mb-3">Menu</h2>

    <div class="list-group">

        <a href="${pageContext.request.contextPath}/liste/create"
           class="list-group-item list-group-item-action">
            ➕ Créer une nouvelle liste de cadeaux
        </a>

        <a href="${pageContext.request.contextPath}/liste/all"
           class="list-group-item list-group-item-action">
            📋 Voir mes listes
        </a>

        <a href="${pageContext.request.contextPath}/liste/invitations"
           class="list-group-item list-group-item-action">
            ✉️ Voir mes invitations
        </a>

		<a href="${pageContext.request.contextPath}/reservation/contributions"
		   class="list-group-item list-group-item-action">
		    💶 Voir mes contributions
		</a>
		
    </div>

</div>

</body>
</html>
