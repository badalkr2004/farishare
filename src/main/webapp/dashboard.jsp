<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - FairShareBU</title>
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
        .user-welcome {
            margin-bottom: 20px;
        }
        .card {
            margin-bottom: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .card-header {
            background-color: #f1f9ff;
            border-bottom: 1px solid #e0f0ff;
        }
        .expense-item {
            border-left: 4px solid #3498db;
            margin-bottom: 10px;
            padding: 10px;
            background-color: #f8f9fa;
            border-radius: 4px;
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
                        <a class="nav-link active" href="${pageContext.request.contextPath}/dashboard"><i class="fas fa-home"></i> Dashboard</a>
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/notifications">
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
                <div class="user-welcome">
                    <h2>Welcome, ${user.fullName}!</h2>
                    <p>Here's a summary of your expenses and groups.</p>
                </div>
                
                <div class="row">
                    <!-- Quick Actions -->
                    <div class="col-md-12 mb-4">
                        <div class="card">
                            <div class="card-header">
                                <h5>Quick Actions</h5>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-3">
                                        <a href="${pageContext.request.contextPath}/expenses/create" class="btn btn-primary w-100 mb-2">
                                            <i class="fas fa-plus"></i> Add Expense
                                        </a>
                                    </div>
                                    <div class="col-md-3">
                                        <a href="${pageContext.request.contextPath}/payments/create" class="btn btn-success w-100 mb-2">
                                            <i class="fas fa-money-bill"></i> Make Payment
                                        </a>
                                    </div>
                                    <div class="col-md-3">
                                        <a href="${pageContext.request.contextPath}/groups/create" class="btn btn-info w-100 mb-2">
                                            <i class="fas fa-users"></i> Create Group
                                        </a>
                                    </div>
                                    <div class="col-md-3">
                                        <a href="${pageContext.request.contextPath}/expenses/settle" class="btn btn-warning w-100 mb-2">
                                            <i class="fas fa-calculator"></i> Settle Up
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="row">
                    <!-- Quick Statistics -->
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-header">
                                <h5>Total Balance</h5>
                            </div>
                            <div class="card-body">
                                <h3 class="card-title">₹ 0.00</h3>
                                <p class="card-text">You're all settled up!</p>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-header">
                                <h5>You Owe</h5>
                            </div>
                            <div class="card-body">
                                <h3 class="card-title">₹ 0.00</h3>
                                <p class="card-text">You don't owe anything.</p>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-header">
                                <h5>You Are Owed</h5>
                            </div>
                            <div class="card-body">
                                <h3 class="card-title">₹ 0.00</h3>
                                <p class="card-text">No one owes you money.</p>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="row mt-4">
                    <!-- Your Groups -->
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <h5>Your Groups</h5>
                                <button class="btn btn-sm btn-primary"><i class="fas fa-plus"></i> Create Group</button>
                            </div>
                            <div class="card-body">
                                <p class="text-muted">You haven't created any groups yet. Create a group to start sharing expenses.</p>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Recent Expenses -->
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <h5>Recent Expenses</h5>
                                <button class="btn btn-sm btn-primary"><i class="fas fa-plus"></i> Add Expense</button>
                            </div>
                            <div class="card-body">
                                <p class="text-muted">No recent expenses. Add an expense to get started.</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Groups Card -->
                <div class="col-md-4 mb-4">
                    <div class="card h-100 shadow-sm">
                        <div class="card-body">
                            <h5 class="card-title"><i class="fas fa-users text-primary"></i> Groups</h5>
                            <p class="card-text">Manage your expense sharing groups. Create new groups, add members, and track shared expenses.</p>
                            <a href="${pageContext.request.contextPath}/groups/" class="btn btn-primary">View Groups</a>
                        </div>
                    </div>
                </div>

                <!-- Recent Activity -->
                <div class="row mt-4">
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <h5><i class="fas fa-bell"></i> Recent Notifications</h5>
                                <a href="${pageContext.request.contextPath}/notifications" class="btn btn-sm btn-link">View All</a>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty recentNotifications}">
                                        <c:forEach items="${recentNotifications}" var="notification">
                                            <div class="notification-item p-2 border-bottom">
                                                <p class="mb-1">${notification.message}</p>
                                                <small class="text-muted">${notification.createdAt}</small>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted">No new notifications</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <h5><i class="fas fa-exchange-alt"></i> Recent Payments</h5>
                                <a href="${pageContext.request.contextPath}/payments" class="btn btn-sm btn-link">View All</a>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty recentPayments}">
                                        <c:forEach items="${recentPayments}" var="payment">
                                            <div class="payment-item p-2 border-bottom">
                                                <p class="mb-1">
                                                    <c:choose>
                                                        <c:when test="${payment.payer.userId eq user.userId}">
                                                            You paid ${payment.receiver.fullName}
                                                        </c:when>
                                                        <c:otherwise>
                                                            ${payment.payer.fullName} paid you
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <span class="float-end">₹${payment.amount}</span>
                                                </p>
                                                <small class="text-muted">${payment.paymentDate}</small>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted">No recent payments</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 