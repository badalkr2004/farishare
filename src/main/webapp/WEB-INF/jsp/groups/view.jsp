<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${group.name} - FairShareBU</title>
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
        .group-header {
            background-color: white;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            padding: 20px;
            margin-bottom: 20px;
            position: relative;
        }
        .group-image {
            width: 100px;
            height: 100px;
            object-fit: cover;
            border-radius: 50%;
        }
        .group-image-placeholder {
            width: 100px;
            height: 100px;
            background-color: #e9ecef;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .group-tabs {
            margin-bottom: 20px;
        }
        .tab-content {
            background-color: white;
            border-radius: 10px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }
        .member-card {
            background-color: #f8f9fa;
            border-radius: 5px;
            padding: 10px;
            margin-bottom: 10px;
            display: flex;
            align-items: center;
        }
        .member-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            margin-right: 10px;
            background-color: #dee2e6;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .expense-item {
            border-left: 3px solid #3498db;
            padding: 10px;
            margin-bottom: 10px;
            background-color: #f8f9fa;
            border-radius: 0 5px 5px 0;
        }
        .invite-form {
            margin-top: 20px;
            padding: 15px;
            background-color: #f1f9ff;
            border-radius: 5px;
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
                    <h2>Group Details</h2>
                    <a href="${pageContext.request.contextPath}/groups/" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Back to Groups
                    </a>
                </div>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success">${success}</div>
                </c:if>
                
                <!-- Group Header -->
                <div class="group-header">
                    <div class="row">
                        <div class="col-md-2 text-center">
                            <c:choose>
                                <c:when test="${empty group.groupImage}">
                                    <div class="group-image-placeholder">
                                        <i class="fas fa-users fa-3x text-secondary"></i>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/uploads/groups/${group.groupImage}" class="group-image" alt="${group.name}">
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="col-md-10">
                            <h2>${group.name}</h2>
                            <p>
                                <c:choose>
                                    <c:when test="${empty group.description}">
                                        <span class="text-muted">No description</span>
                                    </c:when>
                                    <c:otherwise>
                                        ${group.description}
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <p>
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
                            <p>
                                <small class="text-muted">
                                    <i class="fas fa-user"></i> Created by ${group.creator.fullName} on 
                                    ${group.createdDate.month} ${group.createdDate.dayOfMonth}, ${group.createdDate.year}
                                </small>
                            </p>
                            <c:if test="${group.creator.userId == sessionScope.user.userId}">
                                <div class="mt-2">
                                    <a href="${pageContext.request.contextPath}/groups/edit/${group.groupId}" class="btn btn-sm btn-warning">
                                        <i class="fas fa-edit"></i> Edit Group
                                    </a>
                                    <button type="button" class="btn btn-sm btn-danger" data-bs-toggle="modal" data-bs-target="#deleteGroupModal">
                                        <i class="fas fa-trash"></i> Delete Group
                                    </button>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
                
                <!-- Group Tabs -->
                <ul class="nav nav-tabs group-tabs" id="groupTabs" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link active" id="members-tab" data-bs-toggle="tab" data-bs-target="#members" type="button" role="tab" aria-controls="members" aria-selected="true">
                            <i class="fas fa-users"></i> Members (${group.members.size()})
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="expenses-tab" data-bs-toggle="tab" data-bs-target="#expenses" type="button" role="tab" aria-controls="expenses" aria-selected="false">
                            <i class="fas fa-money-bill-wave"></i> Recent Expenses
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="balances-tab" data-bs-toggle="tab" data-bs-target="#balances" type="button" role="tab" aria-controls="balances" aria-selected="false">
                            <i class="fas fa-balance-scale"></i> Balances
                        </button>
                    </li>
                </ul>
                
                <div class="tab-content" id="groupTabsContent">
                    <!-- Members Tab -->
                    <div class="tab-pane fade show active" id="members" role="tabpanel" aria-labelledby="members-tab">
                        <div class="row">
                            <div class="col-md-8">
                                <h4>Members</h4>
                                <div class="member-list">
                                    <c:forEach var="member" items="${group.members}">
                                        <div class="member-card">
                                            <div class="member-avatar">
                                                <c:choose>
                                                    <c:when test="${empty member.profilePicture}">
                                                        <i class="fas fa-user"></i>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${pageContext.request.contextPath}/uploads/profiles/${member.profileImage}" alt="${member.fullName}" style="width: 100%; height: 100%; border-radius: 50%;">
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div>
                                                <strong>${member.fullName}</strong>
                                                <div><small>${member.email}</small></div>
                                                <c:if test="${member.userId == group.creator.userId}">
                                                    <span class="badge bg-primary">Group Creator</span>
                                                </c:if>
                                            </div>
                                            <c:if test="${group.creator.userId == sessionScope.user.userId && member.userId != group.creator.userId}">
                                                <div class="ms-auto">
                                                    <button class="btn btn-sm btn-outline-danger" data-bs-toggle="modal" data-bs-target="#removeMemberModal" data-member-id="${member.userId}" data-member-name="${member.fullName}">
                                                        <i class="fas fa-user-minus"></i> Remove
                                                    </button>
                                                </div>
                                            </c:if>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                            
                            <div class="col-md-4">
                                <c:if test="${group.creator.userId == sessionScope.user.userId}">
                                    <div class="invite-form">
                                        <h5>Invite New Members</h5>
                                        <form action="${pageContext.request.contextPath}/groups/invite/${group.groupId}" method="post">
                                            <div class="mb-3">
                                                <label for="inviteEmail" class="form-label">Email Address</label>
                                                <input type="email" class="form-control" id="inviteEmail" name="email" required>
                                            </div>
                                            <button type="submit" class="btn btn-primary">Send Invitation</button>
                                        </form>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Expenses Tab -->
                    <div class="tab-pane fade" id="expenses" role="tabpanel" aria-labelledby="expenses-tab">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h4>Recent Expenses</h4>
                            <a href="${pageContext.request.contextPath}/expenses/create?groupId=${group.groupId}" class="btn btn-success">
                                <i class="fas fa-plus"></i> Add Expense
                            </a>
                        </div>
                        
                        <c:choose>
                            <c:when test="${empty recentExpenses}">
                                <div class="alert alert-info">
                                    No expenses have been added to this group yet.
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="expense-list">
                                    <c:forEach var="expense" items="${recentExpenses}">
                                        <div class="expense-item">
                                            <div class="d-flex justify-content-between">
                                                <div>
                                                    <h6>${expense.description}</h6>
                                                    <div><small class="text-muted">Paid by ${expense.paidBy.fullName}</small></div>
                                                    <div><small class="text-muted">${expense.date.month} ${expense.date.dayOfMonth}, ${expense.date.year}</small></div>
                                                </div>
                                                <div class="text-end">
                                                    <h5>$${expense.amount}</h5>
                                                    <a href="${pageContext.request.contextPath}/expenses/view/${expense.expenseId}" class="btn btn-sm btn-outline-primary">View Details</a>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                    <div class="text-center mt-3">
                                        <a href="${pageContext.request.contextPath}/expenses/group?id=${group.groupId}" class="btn btn-outline-primary">
                                            View All Expenses
                                        </a>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    
                    <!-- Balances Tab -->
                    <div class="tab-pane fade" id="balances" role="tabpanel" aria-labelledby="balances-tab">
                        <h4>Balances</h4>
                        <c:choose>
                            <c:when test="${empty balances}">
                                <div class="alert alert-info">
                                    No balances to display. Add expenses to see balances between group members.
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th>Member</th>
                                                <th>Balance</th>
                                                <th>Status</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="balance" items="${balances}">
                                                <tr>
                                                    <td>${balance.user.fullName}</td>
                                                    <td>
                                                        <span class="${balance.amount >= 0 ? 'text-success' : 'text-danger'}">
                                                            ${balance.amount >= 0 ? '+' : ''}$${Math.abs(balance.amount)}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${balance.amount > 0}">
                                                                <span class="badge bg-success">Getting back</span>
                                                            </c:when>
                                                            <c:when test="${balance.amount < 0}">
                                                                <span class="badge bg-danger">Owes</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-secondary">Settled</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                <div class="text-center mt-3">
                                    <a href="${pageContext.request.contextPath}/expenses/settle?groupId=${group.groupId}" class="btn btn-primary">
                                        <i class="fas fa-calculator"></i> Settle Up
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Delete Group Modal -->
    <div class="modal fade" id="deleteGroupModal" tabindex="-1" aria-labelledby="deleteGroupModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="deleteGroupModalLabel">Confirm Delete</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to delete the group "${group.name}"? This action cannot be undone and all associated expenses will be deleted.
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form action="${pageContext.request.contextPath}/groups/delete/${group.groupId}" method="post">
                        <button type="submit" class="btn btn-danger">Delete Group</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Remove Member Modal -->
    <div class="modal fade" id="removeMemberModal" tabindex="-1" aria-labelledby="removeMemberModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="removeMemberModalLabel">Confirm Remove Member</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Are you sure you want to remove <span id="memberName"></span> from this group?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <form action="${pageContext.request.contextPath}/groups/removeMember/${group.groupId}" method="post">
                        <input type="hidden" id="memberId" name="memberId" value="">
                        <button type="submit" class="btn btn-danger">Remove Member</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Remove member modal functionality
        const removeMemberModal = document.getElementById('removeMemberModal');
        if (removeMemberModal) {
            removeMemberModal.addEventListener('show.bs.modal', function (event) {
                const button = event.relatedTarget;
                const memberId = button.getAttribute('data-member-id');
                const memberName = button.getAttribute('data-member-name');
                
                document.getElementById('memberName').textContent = memberName;
                document.getElementById('memberId').value = memberId;
            });
        }
    </script>
</body>
</html> 