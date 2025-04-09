-- Execute these statements to update your existing database to match the code

-- Updates notification table columns to match code
ALTER TABLE notifications CHANGE userId user_id INT;
ALTER TABLE notifications CHANGE notificationId notification_id INT AUTO_INCREMENT;
ALTER TABLE notifications CHANGE isRead is_read BOOLEAN DEFAULT FALSE;
ALTER TABLE notifications CHANGE timestamp created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Updates expense_participants table columns to match code
ALTER TABLE expense_participants CHANGE expenseId expense_id INT;
ALTER TABLE expense_participants CHANGE userId user_id INT;
ALTER TABLE expense_participants CHANGE share share_amount DECIMAL(10, 2);

-- Updates expenses table columns to match code
ALTER TABLE expenses CHANGE paidById paid_by INT;
ALTER TABLE expenses CHANGE groupId group_id INT;
ALTER TABLE expenses CHANGE date created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE expenses CHANGE receiptImage receipt_image VARCHAR(255);
ALTER TABLE expenses CHANGE paymentMethod payment_method VARCHAR(50);

-- Note: If any of these column names don't exist in your database, you'll need to remove those statements
-- or create the columns first before renaming them. 