<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Group - FairShareBU</title>
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
        .form-card {
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            background-color: white;
            padding: 20px;
        }
        .form-label {
            font-weight: 500;
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
                    <h2>Create New Group</h2>
                    <a href="${pageContext.request.contextPath}/groups/" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Back to Groups
                    </a>
                </div>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                
                <div class="form-card">
                    <form action="${pageContext.request.contextPath}/groups/create" method="post" enctype="multipart/form-data">
                        <div class="mb-3">
                            <label for="name" class="form-label">Group Name*</label>
                            <input type="text" class="form-control" id="name" name="name" required value="${group.name}">
                            <div class="form-text">Give your group a descriptive name.</div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" id="description" name="description" rows="3">${group.description}</textarea>
                            <div class="form-text">Provide a brief description of the group's purpose.</div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="location" class="form-label">Location</label>
                            <input type="text" class="form-control" id="location" name="location" value="${group.location}">
                            <div class="form-text">Optional: Add a location for your group.</div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="groupImage" class="form-label">Group Image</label>
                            <input type="file" class="form-control" id="groupImage" name="groupImage" accept="image/*">
                            <div class="form-text">Optional: Upload an image for your group.</div>
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Add Members</label>
                            <div class="border p-3 rounded mb-3">
                                <div id="membersList">
                                    <!-- Initial member input field -->
                                    <div class="input-group mb-2">
                                        <input type="email" class="form-control" name="members[0]" placeholder="Enter email address">
                                        <button type="button" class="btn btn-outline-danger remove-member" disabled><i class="fas fa-times"></i></button>
                                    </div>
                                </div>
                                <button type="button" id="addMember" class="btn btn-outline-primary btn-sm mt-2">
                                    <i class="fas fa-plus"></i> Add Another Member
                                </button>
                                <div class="form-text">Add members by their email address.</div>
                            </div>
                        </div>
                        
                        <div class="mb-3 form-check">
                            <input type="checkbox" class="form-check-input" id="privateGroup" name="privateGroup" value="true">
                            <label class="form-check-label" for="privateGroup">Make this group private</label>
                            <div class="form-text">Private groups are only visible to members.</div>
                        </div>
                        
                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary">Create Group</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            let memberCount = 1;
            
            // Add member button functionality
            document.getElementById('addMember').addEventListener('click', function() {
                const membersList = document.getElementById('membersList');
                const newMember = document.createElement('div');
                newMember.className = 'input-group mb-2';
                newMember.innerHTML = `
                    <input type="email" class="form-control" name="members[${memberCount}]" placeholder="Enter email address">
                    <button type="button" class="btn btn-outline-danger remove-member"><i class="fas fa-times"></i></button>
                `;
                membersList.appendChild(newMember);
                memberCount++;
                
                // Add event listeners to remove buttons
                document.querySelectorAll('.remove-member').forEach(function(button) {
                    if (!button.hasAttribute('disabled')) {
                        button.addEventListener('click', function() {
                            this.parentElement.remove();
                        });
                    }
                });
            });
        });
    </script>
</body>
</html> 