<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Expense - FairShareBU</title>
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
        .expense-card {
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 20px;
        }
        .expense-header {
            background-color: #f1f9ff;
            padding: 15px;
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
            border-bottom: 1px solid #e0f0ff;
        }
        .participant-list {
            max-height: 300px;
            overflow-y: auto;
        }
        .receipt-image {
            max-width: 100%;
            max-height: 300px;
            border-radius: 5px;
            margin-top: 10px;
        }
        .status-pending {
            color: #f39c12;
        }
        .status-settled {
            color: #27ae60;
        }
        .action-buttons {
            margin-top: 20px;
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
                        <a class="nav-link" href="${pageContext.request.contextPath}/groups/"><i class="fas fa-users"></i> Groups</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/expenses/"><i class="fas fa-money-bill-wave"></i> Expenses</a>
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
                    <div>
                        <h2>Expense Details</h2>
                        <p class="text-muted">
                            <a href="${pageContext.request.contextPath}/expenses/">All Expenses</a> / 
                            <a href="${pageContext.request.contextPath}/expenses/group?id=${expense.group.groupId}">${expense.group.name}</a> / 
                            Expense #${expense.expenseId}
                        </p>
                    </div>
                    
                    <c:if test="${expense.paidBy.userId == sessionScope.user.userId}">
                        <div class="action-buttons">
                            <a href="${pageContext.request.contextPath}/expenses/edit/${expense.expenseId}" class="btn btn-warning">
                                <i class="fas fa-edit"></i> Edit
                            </a>
                            <button type="button" class="btn btn-danger" data-bs-toggle="modal" data-bs-target="#deleteExpenseModal">
                                <i class="fas fa-trash"></i> Delete
                            </button>
                        </div>
                    </c:if>
                </div>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                
                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>
                
                <!-- Expense Details Card -->
                <div class="card expense-card">
                    <div class="expense-header">
                        <div class="row">
                            <div class="col-md-8">
                                <h3>${expense.description}</h3>
                                <p class="text-muted">
                                    <i class="fas fa-calendar"></i> ${expense.createdAt.monthValue}/${expense.createdAt.dayOfMonth}/${expense.createdAt.year}
                                    <span class="ms-3">
                                        <i class="fas fa-users"></i> Group: ${expense.group.name}
                                    </span>
                                </p>
                            </div>
                            <div class="col-md-4 text-end">
                                <h4>₹${expense.amount}</h4>
                                <p>
                                    <span class="badge bg-primary">${expense.paymentMethod}</span>
                                    <c:choose>
                                        <c:when test="${expense.status == 'PENDING'}">
                                            <span class="badge bg-warning">Pending</span>
                                        </c:when>
                                        <c:when test="${expense.status == 'SETTLED'}">
                                            <span class="badge bg-success">Settled</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${expense.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h5><i class="fas fa-info-circle"></i> Details</h5>
                                <table class="table">
                                    <tr>
                                        <th>Paid By:</th>
                                        <td>${expense.paidBy.fullName}</td>
                                    </tr>
                                    <tr>
                                        <th>Amount:</th>
                                        <td>₹${expense.amount}</td>
                                    </tr>
                                    <tr>
                                        <th>Date:</th>
                                        <td>${expense.createdAt.monthValue}/${expense.createdAt.dayOfMonth}/${expense.createdAt.year}</td>
                                    </tr>
                                    <tr>
                                        <th>Payment Method:</th>
                                        <td>${expense.paymentMethod}</td>
                                    </tr>
                                    <tr>
                                        <th>Status:</th>
                                        <td>
                                            <c:choose>
                                                <c:when test="${expense.status == 'PENDING'}">
                                                    <span class="status-pending"><i class="fas fa-clock"></i> Pending</span>
                                                </c:when>
                                                <c:when test="${expense.status == 'SETTLED'}">
                                                    <span class="status-settled"><i class="fas fa-check-circle"></i> Settled</span>
                                                </c:when>
                                                <c:otherwise>
                                                    ${expense.status}
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </table>
                                
                                <c:if test="${not empty expense.receiptImage}">
                                    <h5 class="mt-4"><i class="fas fa-receipt"></i> Receipt</h5>
                                    <img src="${pageContext.request.contextPath}/uploads/receipts/${expense.receiptImage}" alt="Receipt" class="receipt-image">
                                </c:if>
                            </div>
                            
                            <div class="col-md-6">
                                <h5><i class="fas fa-users"></i> Split Details</h5>
                                <div class="participant-list">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th>Participant</th>
                                                <th>Share Amount</th>
                                                <th>Status</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="participant" items="${expense.participants}">
                                                <tr>
                                                    <td>
                                                        ${participant.fullName}
                                                        <c:if test="${participant.userId == expense.paidBy.userId}">
                                                            <span class="badge bg-info">Paid</span>
                                                        </c:if>
                                                    </td>
                                                    <td>₹${expense.getShare(participant)}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${participant.userId == expense.paidBy.userId || expense.status == 'SETTLED'}">
                                                                <span class="text-success"><i class="fas fa-check-circle"></i> Settled</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-warning"><i class="fas fa-clock"></i> Pending</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                
                                <!-- Settle Up Button -->
                                <c:if test="${expense.status == 'PENDING' && sessionScope.user.userId != expense.paidBy.userId}">
                                    <div class="text-center mt-4">
                                        <c:set var="userShare" value="${expense.getShare(sessionScope.user)}" />
                                        <c:if test="${userShare > 0}">
                                            <a href="${pageContext.request.contextPath}/expenses/settle?expenseId=${expense.expenseId}" class="btn btn-success btn-lg">
                                                <i class="fas fa-check-circle"></i> Settle Up (₹${userShare})
                                            </a>
                                        </c:if>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Back to Group Button -->
                <div class="mt-4">
                    <a href="${pageContext.request.contextPath}/expenses/group?id=${expense.group.groupId}" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Back to Group Expenses
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Delete Expense Confirmation Modal -->
    <div class="modal fade" id="deleteExpenseModal" tabindex="-1" aria-labelledby="deleteExpenseModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteExpenseModalLabel">Confirm Deletion</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <p>Are you sure you want to delete this expense? This action cannot be undone.</p>
                    <p><strong>${expense.description}</strong> - ₹${expense.amount}</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <a href="${pageContext.request.contextPath}/expenses/delete/${expense.expenseId}" class="btn btn-danger">Delete Expense</a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 