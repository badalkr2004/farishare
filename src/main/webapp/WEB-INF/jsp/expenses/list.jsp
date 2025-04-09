<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Expenses - FairShareBU</title>
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
        .expense-item {
            padding: 15px;
            border-bottom: 1px solid #eee;
        }
        .expense-item:last-child {
            border-bottom: none;
        }
        .expense-amount {
            font-weight: bold;
        }
        .expense-date {
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
                        <a class="nav-link active" href="${pageContext.request.contextPath}/expenses"><i class="fas fa-money-bill-wave"></i> Expenses</a>
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
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2>Expenses</h2>
                    <a href="${pageContext.request.contextPath}/expenses/create" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Add Expense
                    </a>
                </div>
                
                <div class="row mb-4">
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">Total You Owe</h5>
                                <h3 class="text-danger">₹${totalOwed != null ? totalOwed : '0.00'}</h3>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">Total You're Owed</h5>
                                <h3 class="text-success">₹${totalOwing != null ? totalOwing : '0.00'}</h3>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">Net Balance</h5>
                                <h3 class="${netBalance > 0 ? 'text-success' : netBalance < 0 ? 'text-danger' : ''}">
                                    ₹${netBalance != null ? netBalance : '0.00'}
                                </h3>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div class="card">
                    <div class="card-header">
                        <ul class="nav nav-tabs card-header-tabs">
                            <li class="nav-item">
                                <a class="nav-link active" href="#all-expenses" data-bs-toggle="tab">All Expenses</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#you-paid" data-bs-toggle="tab">You Paid</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="#you-owe" data-bs-toggle="tab">You Owe</a>
                            </li>
                        </ul>
                    </div>
                    <div class="card-body">
                        <div class="tab-content">
                            <!-- All Expenses -->
                            <div class="tab-pane fade show active" id="all-expenses">
                                <c:choose>
                                    <c:when test="${not empty expenses}">
                                        <div class="list-group">
                                            <c:forEach items="${expenses}" var="expense">
                                                <a href="${pageContext.request.contextPath}/expenses/view/${expense.expenseId}" class="list-group-item list-group-item-action expense-item">
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <h5>${expense.description}</h5>
                                                            <p class="mb-1">
                                                                Group: ${expense.group.name} | 
                                                                <c:choose>
                                                                    <c:when test="${expense.paidBy.userId eq user.userId}">
                                                                        You paid
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        ${expense.paidBy.fullName} paid
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </p>
                                                            <p class="expense-date">
                                                                ${expense.createdAt.monthValue}/${expense.createdAt.dayOfMonth}/${expense.createdAt.year}
                                                            </p>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <span class="badge bg-primary">
                                                                ${expense.participants.size()} participants
                                                            </span>
                                                        </div>
                                                        <div class="col-md-3 text-end">
                                                            <h4 class="expense-amount">₹${expense.amount}</h4>
                                                            <c:if test="${expense.paidBy.userId eq user.userId && not empty expense.participants}">
                                                                <small class="text-muted">
                                                                    You are owed ₹${expense.amount - (expense.amount / expense.participants.size())}
                                                                </small>
                                                            </c:if>
                                                            <c:if test="${expense.paidBy.userId ne user.userId}">
                                                                <small class="text-muted">
                                                                    You owe ₹${expense.amount / expense.participants.size()}
                                                                </small>
                                                            </c:if>
                                                        </div>
                                                    </div>
                                                </a>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-5">
                                            <i class="fas fa-receipt fa-4x text-muted mb-3"></i>
                                            <h4>No expenses yet</h4>
                                            <p>Start adding expenses to track your spending and split with friends.</p>
                                            <a href="${pageContext.request.contextPath}/expenses/create" class="btn btn-primary mt-2">
                                                Add your first expense
                                            </a>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <!-- You Paid -->
                            <div class="tab-pane fade" id="you-paid">
                                <c:choose>
                                    <c:when test="${not empty youPaidExpenses}">
                                        <div class="list-group">
                                            <c:forEach items="${youPaidExpenses}" var="expense">
                                                <a href="${pageContext.request.contextPath}/expenses/view/${expense.expenseId}" class="list-group-item list-group-item-action expense-item">
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <h5>${expense.description}</h5>
                                                            <p class="mb-1">
                                                                Group: ${expense.group.name}
                                                            </p>
                                                            <p class="expense-date">
                                                                ${expense.createdAt.monthValue}/${expense.createdAt.dayOfMonth}/${expense.createdAt.year}
                                                            </p>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <span class="badge bg-primary">
                                                                ${expense.participants.size()} participants
                                                            </span>
                                                        </div>
                                                        <div class="col-md-3 text-end">
                                                            <h4 class="expense-amount">₹${expense.amount}</h4>
                                                            <small class="text-muted">
                                                                You are owed ₹${expense.amount - (expense.amount / expense.participants.size())}
                                                            </small>
                                                        </div>
                                                    </div>
                                                </a>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-5">
                                            <i class="fas fa-hand-holding-usd fa-4x text-muted mb-3"></i>
                                            <h4>No expenses paid by you</h4>
                                            <p>When you pay for expenses, they will appear here.</p>
                                            <a href="${pageContext.request.contextPath}/expenses/create" class="btn btn-primary mt-2">
                                                Add an expense
                                            </a>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <!-- You Owe -->
                            <div class="tab-pane fade" id="you-owe">
                                <c:choose>
                                    <c:when test="${not empty youOweExpenses}">
                                        <div class="list-group">
                                            <c:forEach items="${youOweExpenses}" var="expense">
                                                <a href="${pageContext.request.contextPath}/expenses/view/${expense.expenseId}" class="list-group-item list-group-item-action expense-item">
                                                    <div class="row">
                                                        <div class="col-md-6">
                                                            <h5>${expense.description}</h5>
                                                            <p class="mb-1">
                                                                Group: ${expense.group.name} | 
                                                                ${expense.paidBy.fullName} paid
                                                            </p>
                                                            <p class="expense-date">
                                                                ${expense.createdAt.monthValue}/${expense.createdAt.dayOfMonth}/${expense.createdAt.year}
                                                            </p>
                                                        </div>
                                                        <div class="col-md-3">
                                                            <span class="badge bg-primary">
                                                                ${expense.participants.size()} participants
                                                            </span>
                                                        </div>
                                                        <div class="col-md-3 text-end">
                                                            <h4 class="expense-amount">₹${expense.amount}</h4>
                                                            <small class="text-danger">
                                                                You owe ₹${expense.amount / expense.participants.size()}
                                                            </small>
                                                        </div>
                                                    </div>
                                                </a>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center py-5">
                                            <i class="fas fa-check-circle fa-4x text-success mb-3"></i>
                                            <h4>No expenses you owe</h4>
                                            <p>You're all settled up! No outstanding balances.</p>
                                        </div>
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