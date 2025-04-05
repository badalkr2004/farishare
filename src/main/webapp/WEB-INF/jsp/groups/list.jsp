<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Groups - FairShareBU</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .sidebar {
            height: 100vh;
            background-color: #3498db;
            color: white;
            position: fixed;
            padding-top: 20px;
        }
        .sidebar .nav-link {
            color: rgba(255, 255, 255, 0.8);
            margin-bottom: 10px;
        }
        .sidebar .nav-link:hover {
            color: white;
        }
        .sidebar .nav-link.active {
            color: white;
            font-weight: bold;
        }
        .sidebar .nav-link i {
            margin-right: 10px;
        }
        .logo {
            padding: 15px;
            margin-bottom: 30px;
            text-align: center;
        }
        .logo h3 {
            color: white;
        }
        .content {
            margin-left: 240px;
            padding: 20px;
        }
        .group-card {
            margin-bottom: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s;
        }
        .group-card:hover {
            transform: translateY(-5px);
        }
        .group-card .card-header {
            background-color: #f1f9ff;
            border-bottom: 1px solid #e0f0ff;
        }
        .group-image {
            width: 100%;
            height: 120px;
            object-fit: cover;
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
        }
        .create-group-btn {
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="col-md-2 sidebar">
                <div class="logo">
                    <h3>FairShareBU</h3>
                </div>
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard.jsp"><i class="fas fa-home"></i> Dashboard</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/groups/"><i class="fas fa-users"></i> Groups</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/expenses/"><i class="fas fa-money-bill-wave"></i> Expenses</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#"><i class="fas fa-calculator"></i> Settle Up</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/notifications/"><i class="fas fa-bell"></i> Notifications</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#"><i class="fas fa-user"></i> Profile</a>
                    </li>
                    <li class="nav-item mt-5">
                        <a class="nav-link" href="${pageContext.request.contextPath}/auth/logout"><i class="fas fa-sign-out-alt"></i> Logout</a>
                    </li>
                </ul>
            </div>
            
            <!-- Main Content -->
            <div class="col-md-10 content">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2>My Groups</h2>
                    <a href="${pageContext.request.contextPath}/groups/create" class="btn btn-primary create-group-btn">
                        <i class="fas fa-plus"></i> Create Group
                    </a>
                </div>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                
                <div class="row">
                    <c:choose>
                        <c:when test="${empty groups}">
                            <div class="col-12">
                                <div class="alert alert-info">
                                    <p>You don't have any groups yet. Create a group to start sharing expenses with friends!</p>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="group" items="${groups}">
                                <div class="col-md-4">
                                    <div class="card group-card">
                                        <c:choose>
                                            <c:when test="${empty group.groupImage}">
                                                <div class="card-img-top group-image bg-light d-flex align-items-center justify-content-center">
                                                    <i class="fas fa-users fa-3x text-secondary"></i>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/uploads/groups/${group.groupImage}" class="group-image" alt="${group.name}">
                                            </c:otherwise>
                                        </c:choose>
                                        <div class="card-header">
                                            <h5 class="card-title">${group.name}</h5>
                                        </div>
                                        <div class="card-body">
                                            <p class="card-text">
                                                <c:choose>
                                                    <c:when test="${empty group.description}">
                                                        <span class="text-muted">No description</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${group.description}
                                                    </c:otherwise>
                                                </c:choose>
                                            </p>
                                            <p class="card-text">
                                                <small class="text-muted">
                                                    <i class="fas fa-map-marker-alt"></i> 
                                                    <c:choose>
                                                        <c:when test="${empty group.location}">
                                                            No location specified
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${group.location}
                                                        </c:otherwise>
                                                    </c:choose>
                                                </small>
                                            </p>
                                            <p class="card-text">
                                                <small class="text-muted">
                                                    <i class="fas fa-user"></i> Created by ${group.creator.fullName}
                                                </small>
                                            </p>
                                            <p class="card-text">
                                                <small class="text-muted">
                                                    <i class="fas fa-users"></i> ${group.members.size()} members
                                                </small>
                                            </p>
                                        </div>
                                        <div class="card-footer">
                                            <a href="${pageContext.request.contextPath}/groups/view/${group.groupId}" class="btn btn-sm btn-primary">View Details</a>
                                            <a href="${pageContext.request.contextPath}/expenses/group?id=${group.groupId}" class="btn btn-sm btn-success">Expenses</a>
                                            <c:if test="${group.creator.userId == sessionScope.user.userId}">
                                                <a href="${pageContext.request.contextPath}/groups/edit/${group.groupId}" class="btn btn-sm btn-warning">Edit</a>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 