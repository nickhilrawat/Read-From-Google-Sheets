package com.googleSheets.demo;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import javax.activation.FileDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class SheetsAndJava {
    
    private static Sheets sheetsService;
    private static String APPLICATION_NAME = "Google sheets";
    private static String SPREADSHEET_ID = "1VbUZzO9XWiwQiwvCiJK-P64xeqfpbTB7Xkwx_VKDKhg";
    
    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = SheetsAndJava.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance(), new InputStreamReader(in));

        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
                clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
    
    public static  Sheets getSheetsService() throws IOException, GeneralSecurityException{
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    public static void main(String[] args) throws IOException, GeneralSecurityException{
        sheetsService = getSheetsService();
        String range = "Sheet1!A3:F6"; // sheetname!from:to
        
        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID,range).execute();
        
        List<List<Object>> values = response.getValues();
        
        if(values == null || values.isEmpty()){
            System.out.println("No data found");
        } else{
            for(List row : values){
                System.out.printf("%s %s from %s \n",row.get(2),row.get(3),row.get(4));
            }
        }
        
    }
}
