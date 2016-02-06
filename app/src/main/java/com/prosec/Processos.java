package com.prosec;

import android.app.Activity;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gusta on 14/10/2015.
 */
public class Processos extends Activity {

    public String Listar ()  throws IOException{
          AssetManager assetManager = getResources().getAssets();
        InputStream inputStream;
        inputStream = assetManager.open("arquivo.txt");//conteudo do que no diretorio assets
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String recebe_string = "";

        final StringBuilder stringBuilder = new StringBuilder();

        while ((recebe_string = bufferedReader.readLine()) != null) {

            stringBuilder.append(recebe_string); // copiamos a linha
            stringBuilder.append("\n"); // adicionando uma quebra

        }

        return stringBuilder.toString();

    }

}
