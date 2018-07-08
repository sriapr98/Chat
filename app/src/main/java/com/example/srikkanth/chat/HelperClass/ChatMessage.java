package com.example.srikkanth.chat.HelperClass;

/**
 * Created by srikkanth on 14/6/18.
 */

public class ChatMessage {
        private String username;
        private String photoUrl;
        private String message;

        public ChatMessage(String message, String photoUrl, String username) {
            this.username = username;
            this.photoUrl = photoUrl;
            this.message = message;
        }

        public ChatMessage(String message) {
            this.message = message;
            this.photoUrl=null;
            this.username="admin";
        }

        public ChatMessage(){

        }
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

