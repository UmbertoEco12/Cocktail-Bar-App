package com.example.lso_project.Helpers;

public interface IHandleServerResponse {

    // not called on ui thread
    void HandleResponse(String response);
}
