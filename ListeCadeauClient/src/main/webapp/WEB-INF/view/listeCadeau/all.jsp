<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, be.couderiannello.models.ListeCadeau" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Mes listes</title>
</head>
<body>

<jsp:include page="/WEB-INF/view/includes/header.jsp" />

<div class="container mt-4">

    <h2 class="mb-4">Mes listes de cadeaux</h2>

<%
    String error = (String) request.getAttribute("error");
    if (error != null && !error.isBlank()) {
%>
    <div class="alert alert-danger fw-bold"><%= error %></div>
<%
    }
%>

    <%
        List<ListeCadeau> listes = (List<ListeCadeau>) request.getAttribute("listes");

        if (listes == null || listes.isEmpty()) {
    %>
        <p class="text-muted">Aucune liste pour le moment.</p>

    <%
        } else {
            for (ListeCadeau l : listes) {
    %>

        <div class="card mb-3 shadow-sm">
    <div class="card-body d-flex justify-content-between align-items-center">

        <div>
            <h5 class="card-title"><%= l.getTitle() %></h5>
            <p class="card-text text-muted">
                <%= l.getEvenement() %>
            </p>
        </div>

        <div>
            <a href="<%= request.getContextPath() %>/liste/manage?id=<%= l.getId() %>"
               class="btn btn-primary btn-sm">
                Voir la liste
            </a>

            <form action="<%= request.getContextPath() %>/liste/delete"
                  method="post"
                  style="display:inline;"
                  onsubmit="return confirm('Supprimer cette liste ?');">

                <input type="hidden" name="id" value="<%= l.getId() %>" />

                <button type="submit" class="btn btn-danger btn-sm">
                    Supprimer
                </button>
            </form>
        </div>

    </div>
</div>


    <%
            }
        }
    %>
    
</div>

</body>
</html>
