<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, be.couderiannello.models.ListeCadeau, be.couderiannello.models.Personne" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Mes invitations</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

    <h2 class="mb-3">Listes auxquelles vous êtes invités :</h2>

<%
    List<ListeCadeau> listes = (List<ListeCadeau>) request.getAttribute("listesInvite");
    if (listes == null || listes.isEmpty()) {
%>
    <p class="text-muted">Tu n’as aucune invitation pour le moment.</p>
<%
    } else {
%>

    <div class="row">
    <%
        for (ListeCadeau l : listes) {
            Personne creator = l.getCreator();

            String creatorLabel = "Créateur inconnu";
            creatorLabel = creator.getFirstName() + " " + creator.getName();
    %>
        <div class="col-md-6 col-lg-4 mb-3">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h5 class="card-title"><%= l.getTitle() %></h5>

                    <p class="card-text">
                        <b>Événement :</b> <%= l.getEvenement() %><br>
                        <b>Expiration :</b> <%= l.getExpirationDate() %><br>
                        <b>Créée par :</b> <%= creatorLabel %>
                    </p>

                    <a class="btn btn-primary btn-sm"
                       href="<%= request.getContextPath() %>/liste/view?id=<%= l.getId() %>">
                        Ouvrir la liste
                    </a>
                </div>
            </div>
        </div>
    <%
        }
    %>
    </div>

<%
    }
%>

    <div class="mt-3">
        <a class="btn btn-secondary" href="<%= request.getContextPath() %>/home">Retour</a>
    </div>

</div>

</body>
</html>
