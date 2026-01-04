<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Home</title>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />
</head>

<body class="bg-light position-relative">

<%
    String userName = (String) session.getAttribute("firstName");
%>

<div class="container my-5">

    <div class="bg-white rounded-4 shadow-lg p-5">

        <div class="text-center mb-5">
            <h1 class="fw-bold">
                🎁 Bienvenue <span class="text-danger"><%= userName %></span>
            </h1>
            <p class="text-muted mt-2">
                Gérez vos listes de cadeaux, partagez-les et contribuez facilement
            </p>
        </div>

        <div class="row g-4">

            <div class="col-md-6 col-lg-3">
                <a href="${pageContext.request.contextPath}/liste/create"
                   class="text-decoration-none text-dark">
                    <div class="card h-100 shadow-sm text-center p-4 border-0">
                        <div class="fs-1 mb-3">➕🎁</div>
                        <h5 class="fw-bold">Créer une liste</h5>
                        <p class="text-muted small">
                            Préparez une nouvelle liste de cadeaux
                        </p>
                    </div>
                </a>
            </div>

            <div class="col-md-6 col-lg-3">
                <a href="${pageContext.request.contextPath}/liste/all"
                   class="text-decoration-none text-dark">
                    <div class="card h-100 shadow-sm text-center p-4 border-0">
                        <div class="fs-1 mb-3">📋🎁</div>
                        <h5 class="fw-bold">Mes listes</h5>
                        <p class="text-muted small">
                            Consultez et gérez vos listes existantes
                        </p>
                    </div>
                </a>
            </div>

            <div class="col-md-6 col-lg-3">
                <a href="${pageContext.request.contextPath}/liste/invitations"
                   class="text-decoration-none text-dark">
                    <div class="card h-100 shadow-sm text-center p-4 border-0">
                        <div class="fs-1 mb-3">✉️🎉</div>
                        <h5 class="fw-bold">Invitations</h5>
                        <p class="text-muted small">
                            Accédez aux listes où vous êtes invité
                        </p>
                    </div>
                </a>
            </div>

            <div class="col-md-6 col-lg-3">
                <a href="${pageContext.request.contextPath}/reservation/contributions"
                   class="text-decoration-none text-dark">
                    <div class="card h-100 shadow-sm text-center p-4 border-0">
                        <div class="fs-1 mb-3">💶💝</div>
                        <h5 class="fw-bold">Mes contributions</h5>
                        <p class="text-muted small">
                            Suivez les cadeaux auxquels vous avez participé
                        </p>
                    </div>
                </a>
            </div>

        </div>

    </div>

</div>

</body>
</html>
