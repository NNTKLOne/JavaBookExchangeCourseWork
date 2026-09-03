/*
package coursework.fxControllers;

import coursework.hibenateControllers.GenericHibernate;
import coursework.model.Chat;
import coursework.model.Comment;
import coursework.persistence.Database;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import jakarta.persistence.EntityManagerFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    private ListView<Chat> chatListField;  // List of chats

    @FXML
    private ListView<Comment> commentListField;  // List of comments for a selected chat

    @FXML
    private TextField chatTitleField;  // Field for entering chat title

    @FXML
    private TextField commentTitleField;  // Field for entering comment title

    @FXML
    private TextArea commentBodyField;  // Field for entering comment body

    private EntityManagerFactory entityManagerFactory = Database.entityManagerFactory();
    private GenericHibernate hibernate = new GenericHibernate(entityManagerFactory);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillChatList();  // Load chats when the controller is initialized
    }

    */
/*

    public void fillChatList() {
        chatListField.getItems().clear();
        List<Chat> chatList = hibernate.getAllRecords(Chat.class);
        chatListField.getItems().addAll(chatList);
    }

    */
/*

    @FXML
    public void loadCommentsForChat() {
        Chat selectedChat = chatListField.getSelectionModel().getSelectedItem();

        if (selectedChat != null) {
            // Clear the current comment list
            commentListField.getItems().clear();

            // Fetch the comments associated with the selected chat
            List<Comment> commentList = hibernate.getAllRecords(Comment.class);

            // Filter comments to only those belonging to the selected chat
            List<Comment> filteredComments = commentList.stream()
                    .filter(comment -> comment.getChat().getId() == selectedChat.getId())
                    .toList();

            // Add the filtered comments to the ListView
            commentListField.getItems().addAll(filteredComments);
        }
    }

    */
/*

    @FXML
    public void createNewChat() {
        if (chatTitleField.getText().isEmpty()) {
            showError("Chat title is required.");
            return;
        }

        Chat newChat = new Chat();
        newChat.setTitle(chatTitleField.getText());

        hibernate.create(newChat);
        fillChatList();  // Refresh the chat list after adding
        clearChatFields();
    }

    */
/*

    @FXML
    public void addCommentToChat() {
        Chat selectedChat = chatListField.getSelectionModel().getSelectedItem();
        if (selectedChat == null || commentTitleField.getText().isEmpty() || commentBodyField.getText().isEmpty()) {
            showError("Select a chat and enter the comment details.");
            return;
        }

        Comment newComment = new Comment();
        newComment.setTitle(commentTitleField.getText());
        newComment.setBody(commentBodyField.getText());
        newComment.setChat(selectedChat);
        newComment.setTimestamp(LocalDateTime.now());

        // Persist the new comment in the database
        hibernate.create(newComment);

        // Immediately reload the comments to show the newly added comment
        loadCommentsForChat();  // Refresh comments after adding
        clearCommentFields();  // Clear the input fields
    }

    @FXML
    public void updateComment() {
        Comment selectedComment = commentListField.getSelectionModel().getSelectedItem();
        if (selectedComment == null || commentTitleField.getText().isEmpty() || commentBodyField.getText().isEmpty()) {
            showError("Select a comment and enter new details.");
            return;
        }

        selectedComment.setTitle(commentTitleField.getText());
        selectedComment.setBody(commentBodyField.getText());
        hibernate.update(selectedComment);
        loadCommentsForChat();
        clearCommentFields();
    }

    @FXML
    public void deleteComment() {
        Comment selectedComment = commentListField.getSelectionModel().getSelectedItem();

        if (selectedComment == null) {
            showError("Select a comment to delete.");
            return;
        }

        // Remove the comment from the chat's comment list comment is orphaned by chat so it gets deleted from db
        Chat selectedChat = selectedComment.getChat();
        if (selectedChat != null) {
            selectedChat.getComments().remove(selectedComment);
            hibernate.update(selectedChat);
        }

        loadCommentsForChat();
    }

    @FXML
    public void deleteChat() {
        Chat selectedChat = chatListField.getSelectionModel().getSelectedItem();
        if (selectedChat == null) {
            showError("Select a chat to delete.");
            return;
        }

        hibernate.delete(Chat.class, selectedChat.getId());
        fillChatList();
        commentListField.getItems().clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearChatFields() {
        chatTitleField.clear();
    }

    private void clearCommentFields() {
        commentTitleField.clear();
        commentBodyField.clear();
    }
}
*/
