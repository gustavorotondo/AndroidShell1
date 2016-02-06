package com.prosec;

import android.content.ContextWrapper;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.sql.SQLOutput;

/**
 * Created by gusta on 27/10/2015.
 */
public class Web extends MainActivity implements Runnable {

    public void run(){

        FTPClient ftp = new FTPClient();
        try {
            String diretorio = "/data/data/com.prosec/files/WhiteList.txt";
            String nomeArquivo = "WhiteList.txt";
            ftp.connect("ftp.podserver.info", 21);
            ftp.login("podi_16820616", "gu1991");
            FileInputStream arqEnviar = new FileInputStream(diretorio);
            ftp.setFileTransferMode(ftp.BINARY_FILE_TYPE);
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.storeFile(nomeArquivo, arqEnviar);
            ftp.logout();
            ftp.disconnect();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
