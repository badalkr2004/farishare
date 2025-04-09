<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Make Payment - FairShareBU</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .payment-form {
            max-width: 800px;
            margin: 0 auto;
            padding: 30px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        .form-group {
            margin-bottom: 20px;
        }
        .btn-primary {
            background-color: #3498db;
            border-color: #3498db;
        }
        .btn-primary:hover {
            background-color: #2980b9;
            border-color: #2980b9;
        }
    </style>
</head>
<body>
    <div class="container mt-5">
        <div class="payment-form">
            <h2 class="mb-4 text-center">Make a Payment</h2>
            
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>
            
            <form action="${pageContext.request.contextPath}/payments" method="post">
                <input type="hidden" name="action" value="create">
                
                <div class="form-group">
                    <label for="groupId">Group</label>
                    <select id="groupId" name="groupId" class="form-control" required onchange="loadGroupMembers()">
                        <option value="">Select a group</option>
                        <c:forEach items="${groups}" var="group">
                            <option value="${group.groupId}">${group.name}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="receiverId">Paying To</label>
                    <select id="receiverId" name="receiverId" class="form-control" required>
                        <option value="">Select recipient</option>
                        <c:if test="${not empty members}">
                            <c:forEach items="${members}" var="member">
                                <c:if test="${member.userId != user.userId}">
                                    <option value="${member.userId}">${member.fullName}</option>
                                </c:if>
                            </c:forEach>
                        </c:if>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="amount">Amount</label>
                    <div class="input-group">
                        <span class="input-group-text">₹</span>
                        <input type="number" id="amount" name="amount" class="form-control" step="0.01" min="0.01" required>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="description">Description</label>
                    <input type="text" id="description" name="description" class="form-control" required placeholder="What's this payment for?">
                </div>
                
                <div class="form-group">
                    <button type="submit" class="btn btn-primary w-100">Make Payment</button>
                </div>
                
                <div class="text-center mt-3">
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-link">Cancel</a>
                </div>
            </form>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function loadGroupMembers() {
            const groupId = document.getElementById('groupId').value;
            const receiverSelect = document.getElementById('receiverId');
            
            // Clear existing options
            receiverSelect.innerHTML = '<option value="">Select recipient</option>';
            
            if (groupId) {
                // Redirect to this page with the groupId parameter
                window.location.href = '${pageContext.request.contextPath}/payments/create?groupId=' + groupId;
            }
        }
    </script>
</body>
</html> 