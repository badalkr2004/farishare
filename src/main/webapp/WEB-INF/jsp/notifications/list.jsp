<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notifications - FairShareBU</title>
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
        .card {
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .notification-item {
            padding: 15px;
            border-left: 4px solid transparent;
            border-bottom: 1px solid #eee;
            transition: all 0.2s ease;
        }
        .notification-item:hover {
            background-color: #f8f9fa;
        }
        .notification-item.unread {
            background-color: #f0f7ff;
            border-left-color: #3498db;
        }
        .notification-item .title {
            font-weight: bold;
            margin-bottom: 5px;
        }
        .notification-date {
            color: #6c757d;
            font-size: 0.9rem;
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard"><i class="fas fa-home"></i> Dashboard</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/groups"><i class="fas fa-users"></i> Groups</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/expenses"><i class="fas fa-money-bill-wave"></i> Expenses</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/payments"><i class="fas fa-exchange-alt"></i> Payments</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/notifications">
                            <i class="fas fa-bell"></i> Notifications
                            <c:if test="${unreadNotifications > 0}">
                                <span class="badge bg-danger rounded-pill">${unreadNotifications}</span>
                            </c:if>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/profile"><i class="fas fa-user"></i> Profile</a>
                    </li>
                    <li class="nav-item mt-5">
                        <a class="nav-link" href="${pageContext.request.contextPath}/auth/logout"><i class="fas fa-sign-out-alt"></i> Logout</a>
                    </li>
                </ul>
            </div>
            
            <!-- Main Content -->
            <div class="col-md-10 content">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2>Notifications</h2>
                    <form action="${pageContext.request.contextPath}/notifications" method="post">
                        <input type="hidden" name="action" value="markAllRead">
                        <button type="submit" class="btn btn-outline-primary" ${empty notifications ? 'disabled' : ''}>
                            <i class="fas fa-check-double"></i> Mark All as Read
                        </button>
                    </form>
                </div>
                
                <div class="card">
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty notifications}">
                                <div class="list-group">
                                    <c:forEach items="${notifications}" var="notification">
                                        <div class="notification-item ${notification.read ? '' : 'unread'}">
                                            <div class="d-flex justify-content-between">
                                                <div>
                                                    <div class="title">${notification.title}</div>
                                                    <p class="mb-1">${notification.message}</p>
                                                    <p class="notification-date">
                                                        <fmt:formatDate value="${notification.createdAt}" pattern="MMM dd, yyyy HH:mm" />
                                                    </p>
                                                </div>
                                                <div>
                                                    <c:if test="${not notification.read}">
                                                        <form action="${pageContext.request.contextPath}/notifications" method="post" style="display: inline;">
                                                            <input type="hidden" name="action" value="markRead">
                                                            <input type="hidden" name="id" value="${notification.notificationId}">
                                                            <button type="submit" class="btn btn-sm btn-outline-primary">
                                                                <i class="fas fa-check"></i> Mark as Read
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                    <form action="${pageContext.request.contextPath}/notifications" method="post" style="display: inline;">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="id" value="${notification.notificationId}">
                                                        <button type="submit" class="btn btn-sm btn-outline-danger">
                                                            <i class="fas fa-trash"></i>
                                                        </button>
                                                    </form>
                                                    <c:if test="${not empty notification.link}">
                                                        <a href="${pageContext.request.contextPath}${notification.link}" class="btn btn-sm btn-link">
                                                            View
                                                        </a>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center py-5">
                                    <i class="fas fa-bell-slash fa-4x text-muted mb-3"></i>
                                    <h4>No notifications</h4>
                                    <p>When you receive notifications, they will appear here.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 