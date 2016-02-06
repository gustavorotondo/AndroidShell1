package com.prosec;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class MainActivity extends Activity {
    Button btn;
    Button btnDicas;
    TextView out;
    ListView listView;
    boolean teste;
    boolean teste2;
    String virus;
    ArrayList<String> texto = new ArrayList<String>();
    ArrayList<String> textView = new ArrayList<String>();
    ArrayList<String> virusList = new ArrayList<String>();
    String ameaca;
    FileWriter fileWriter = null;



    //NOTIFICAÇÃO OK

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showNotificationOK() {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
        Notification mNotification1 = new Notification.Builder(this)
                .setContentTitle("Aviso")
                .setContentText("Sistema Seguro")
                .setSmallIcon(R.drawable.check)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, mNotification1);
    }

    //NOTIFICAÇÃO NÃO SEGURO

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showNotificationWarning() {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
        Notification mNotification = new Notification.Builder(this)
                .setContentTitle("Alerta")
                .setContentText("Processo Suspeito Detectado")
                .setSmallIcon(R.drawable.alert)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, mNotification);

    }

    //EXECUÇÃO DO COMANDO DE EXIBIR PROCESSOS

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public String Executer(String command) {
        Process p;
        StringBuffer output = new StringBuffer();

        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            String line = "";
            String line2 = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(getFilesDir().getPath() + "/WhiteList.txt"));

            while ((line2 = br.readLine()) != null) { // percorre txt
                texto.add(line2.trim());//adiciona txt em um array

                while ((line = reader.readLine()) != null) { // percore textview
                    //mostrar somente nome do processo
                    String parteFinal = line.substring(line.lastIndexOf(" ") + 1);

                    // ocultar uso de cpu com %, NAME e o PID intruso
                    if (parteFinal.contains("%") || parteFinal.contains("Name") || parteFinal.contains("loop") || parteFinal.contains("daemonsu:")|| parteFinal.contains("kworker")|| parteFinal.startsWith("1") || parteFinal.startsWith("2") || parteFinal.startsWith("3") || parteFinal.startsWith("4") || parteFinal.startsWith("5") || parteFinal.startsWith("6") || parteFinal.startsWith("7") || parteFinal.startsWith("8") || parteFinal.startsWith("9")|| parteFinal.startsWith("0")|| parteFinal.startsWith("-")|| parteFinal.startsWith("/")||parteFinal.contains("-")) {
                        parteFinal = "";
                    }

                    StringTokenizer tokenizer = new StringTokenizer(parteFinal);
                    StringBuilder builder = new StringBuilder();
                    while (tokenizer.hasMoreTokens()) {
                        String string = tokenizer.nextToken();
                        builder.append(string).append('\n');
                    }



                    textView.add(builder.toString().trim());//adiciona textview em um array

                    output.append(builder.toString());
                }

            }



            //busca virus

            ArrayList<String> virusList = new ArrayList<String>();
            for (int i = 0; i < textView.size(); i++) {
                boolean NaoExiste = true;
                for (int ii = 0; ii < texto.size(); ii++) {
                    if (textView.get(i).equals(texto.get(ii))) { // comparação de saida do textView com whiteList
                        NaoExiste = false;
                        teste = false;
                        // break; //termina o loop interior
                    }
                }
                if (NaoExiste) {
                    teste = true;
                    virusList.add(textView.get(i)); //Adiciona à lista de virus
                    break;
                }
            }

//Apresentar os avisos
            /*
            for (String virus : virusList) {
                System.out.println("virus " + virus);
            }
            */

            Object[] objDays = virusList.toArray();
            String[] strDays = Arrays.copyOf(objDays, objDays.length, String[].class);

           // System.out.println(strDays[0]);
            ameaca = strDays[0]; // PASSA PRIMEIRO VIRUS DO ARRAY PARA SISTEMA
            br.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();
        return response;
    }




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        btnDicas = (Button) findViewById(R.id.btnDicas);
        out = (TextView) findViewById(R.id.out);
        listView = (ListView) findViewById(R.id.list);

        try {

            new Thread(new Web()).start();

            System.out.println("veio aqui");
        }catch (Exception e){
            System.out.println("deu erro");
            e.printStackTrace();
        }

        //CRIAR ARQUIVO SE NÃO EXISTIR

        final File file = new File(getFilesDir().getPath() + "/WhiteList.txt"); // crio arquivo bd
        try {

            AssetManager assetManager = getResources().getAssets();
            InputStream inputStream;
            inputStream = assetManager.open("arquivo.txt");//copia conteudo do asstes para bd
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String recebe_string = "";

            final StringBuilder stringBuilder = new StringBuilder();
            while ((recebe_string = bufferedReader.readLine()) != null) {

                stringBuilder.append(recebe_string); // copiamos a linha
                stringBuilder.append("\n"); // adicionando uma quebra

            }

            fileWriter = new FileWriter(file, true);
            fileWriter.append(stringBuilder.toString()); // cria banco de dados
            fileWriter.flush();

        } catch (IOException e) {

            Toast.makeText(this, "Não foi possível copiar o arquivo", Toast.LENGTH_SHORT).show();

        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (Exception e) {
                }
            }
        } if (!file.exists()) {

        } else { // JA EXISTE
            System.out.println("arquivo ja existe");
        }

        btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this,Background.class);
                startService(intent);

                String outp = Executer("top -n 1");
                out.setMovementMethod(new ScrollingMovementMethod());
                out.setText(outp);
                Context ctx = MainActivity.this;
                Log.d("Output", outp);

                //SISTEMA CAPTUROU

                if (teste == true) {
                    out.setText(outp);
                    Log.d("Output", outp);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Sistema Infectado");
                    builder.setIcon(R.drawable.alert);
                    showNotificationWarning(); // NOTIFICAÇÃO DE ALERTA
                    builder.setItems(new CharSequence[]
                                    {"Desinstalar aplicação", "Ignorar", "Detalhes", "Continuar Monitoramento"},
                            new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                                public void onClick(DialogInterface dialog, int which) {

                                    switch (which) {
                                        case 0: //DESINSTALAR
                                            try {
                                                Uri packageURI = Uri.parse("package:" + ameaca);
                                                Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                                                startActivity(uninstallIntent);
                                            } catch (Exception e) {
                                                Toast.makeText(MainActivity.this, "Não foi remover o processo pois o mesmo é do sistema", Toast.LENGTH_LONG).show();
                                            }
                                            // salva aplicativo desinstalado na lista
                                            final File fileBlack = new File(getFilesDir().getPath() + "/BlackList.txt");
                                            if (!fileBlack.exists()) {
                                                try {
                                                    fileWriter = new FileWriter(fileBlack, true);
                                                    fileWriter.append(ameaca);
                                                    fileWriter.append("\n");
                                                    fileWriter.flush();
                                                } catch (Exception e) {

                                                } finally {
                                                    if (fileWriter != null) {
                                                        try {
                                                            fileWriter.close();
                                                            System.out.println("fechei");
                                                        } catch (Exception e) {

                                                        }
                                                    }
                                                }
                                            }
                                            break;
                                        case 1: //IGNORAR
                                            //Adicionar processo no banco de dados
                                            AlertDialog.Builder ignorar = new AlertDialog.Builder(MainActivity.this);
                                            //  System.out.println(teste2);
                                            ignorar.setMessage("Deseja adicionar " + ameaca + " ao banco de dados?");
                                            ignorar.setTitle("Ignorar Processo");

                                            ignorar.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    try {
                                                        final File file2 = new File(getFilesDir().getPath() + "/WhiteList.txt"); // crio arquivo bd
                                                        fileWriter = new FileWriter(file2, true);
                                                        fileWriter.append(ameaca);
                                                        fileWriter.append("\n");
                                                        fileWriter.flush();
                                                    } catch (Exception e) {

                                                    }


                                                }
                                            });
                                            ignorar.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    Toast.makeText(MainActivity.this, "Processo não adicionado", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            ignorar.create().show();


                                            break;
                                        case 2: //DETALHES

                                            final AlertDialog.Builder detalhes = new AlertDialog.Builder(MainActivity.this);
                                            detalhes.setTitle("Detalhes");
                                            detalhes.setIcon(R.drawable.settings);
                                            detalhes.setItems(new CharSequence[]
                                                            {"Permissões", "Play Store", "Validar Aplicação", "Recursos"},
                                                    new DialogInterface.OnClickListener() {
                                                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                                                        public void onClick(DialogInterface dialog, int which) {

                                                            switch (which) {
                                                                case 0: // PERMISSÕES
                                                                    try {
                                                                        // ASSINATURAS

                                                                        //CAPTURA PERMISSÕES NOME DO PACOTE, ICONE E EXIBE NO TEXT VIEW

                                                                        PackageInfo pack = getPackageManager().getPackageInfo(ameaca, PackageManager.GET_PERMISSIONS);
                                                                        PackageManager pm = getPackageManager();
                                                                        ApplicationInfo galleryInfo = pm.getApplicationInfo(ameaca, PackageManager.GET_META_DATA);

                                                                        final String[] requestedPermissions = pack.requestedPermissions;
                                                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                                                                        LayoutInflater inflater = getLayoutInflater();
                                                                        View convertView = (View) inflater.inflate(R.layout.custom, null);
                                                                        alertDialog.setView(convertView);

                                                                        final ListView lv = (ListView) convertView.findViewById(R.id.listView1);
                                                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, requestedPermissions);
                                                                        lv.setAdapter(adapter);

                                                                        //EXIBIÇÃO DE TEXTO AO CLICAR NA PERMISSAO

                                                                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                            @Override
                                                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                                                                String selected = lv.getItemAtPosition(position).toString();

                                                                                if ("android.permission.ACCESS_NETWORK_STATE".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite verificar estado da rede", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.ACCESS_CHECKIN_PROPERTIES".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite escrita e leitura no banco de dados", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.ACCESS_COARSE_LOCATION".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite acessar a localização aproximada do aparelho", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.ACCESS_FINE_LOCATION".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite acessar a localização precisa do aparelho", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.ACCESS_LOCATION_EXTRA_COMMANDS".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite acesso a comandos adicionais para localização", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.ACCESS_NOTIFICATION_POLICY".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite acesso as notificações do aparelho", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.ACCESS_WIFI_STATE".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite verificar estado da conexão WiFi", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.ACCOUNT_MANAGER".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite gerenciar as contas de usuário", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.ADD_VOICEMAIL".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite adicionar mensagens de voz no sistema", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.BATTERY_STATS".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite acessar o estado atual da bateria", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.BLUETOOTH".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Acesso ao rádio bluetooth", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.CALL_PHONE".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite iniciar uma chamada telefônica", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.CAMERA".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite acessar a câmera do aparelho", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.CAPTURE_AUDIO_OUTPUT".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Verificar estado da rede", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.CAPTURE_SECURE_VIDEO_OUTPUT".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite capturar audio", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.CAPTURE_VIDEO_OUTPUT".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite capturar video ", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.CHANGE_CONFIGURATION".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite alterar as configurações do aparelho", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.CHANGE_NETWORK_STATE".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Verificar estado da rede", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.GET_ACCOUNTS".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite capturar as contas do usuário", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.INSTALL_SHORTCUT".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite a instalação de atalhos ", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.INTERNET".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite o acesso a Internet", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.KILL_BACKGROUND_PROCESSES".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite encerrar processos em segundo plano", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.MEDIA_CONTENT_CONTROL".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite controlar o conteudo de mídia", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.MODIFY_PHONE_STATE".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite modificar o estado do telefone", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.NFC".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite o uso do NFC", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.READ_CALENDAR".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite ler o conteúdo do calendário", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.READ_CALL_LOG".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite ler o histórico de ligações", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.READ_CONTACTS".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite ler os contatos da agenda do telefone", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.READ_EXTERNAL_STORAGE".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite acesso ao armazenamento externo do aparelho", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.READ_PHONE_STATE".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite ler o estado do telefone", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.READ_SMS".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite ler as mensagens do aparelho", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.READ_SYNC_SETTINGS".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite ler as configurações sincronizadas", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.READ_VOICEMAIL".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite ler as mensagems de voz", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.REBOOT".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite reiniciar o aparelho", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.RECEIVE_SMS".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite receber SMS", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.SEND_SMS".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite enviar SMS", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.SET_ALARM".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite setar alarmes", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.SET_WALLPAPER".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite altear o papel de parede", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.WRITE_SETTINGS".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite modificar as configurações do aparelho", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.WRITE_EXTERNAL_STORAGE".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite escrever dados no armazenamento externo", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.WAKE_LOCK".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite acordar o aparelho", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.UNINSTALL_SHORTCUT".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite desinstalar os atalhos", Toast.LENGTH_SHORT).show();

                                                                                if ("com.android.launcher.permission.INSTALL_SHORTCUT".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite instalar os atalhos", Toast.LENGTH_SHORT).show();

                                                                                if ("com.google.android.apps.bigtop.permission.C2D_MESSAGE".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Impede que outras aplicações recebam mensagens do aplicativo", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.ACCESS_SUPERUSER".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Permite acesso de super usuário", Toast.LENGTH_SHORT).show();

                                                                                if ("android.permission.VIBRATE".contains(selected))
                                                                                    Toast.makeText(MainActivity.this, "Controla a vibração do aparelho", Toast.LENGTH_SHORT).show();


                                                                            }
                                                                        });

                                                                        //NOME E ICONE DO APP

                                                                        if (null != galleryInfo) {
                                                                            final String label = String.valueOf(pm.getApplicationLabel(galleryInfo));
                                                                            Drawable ic2 = pm.getApplicationIcon(galleryInfo);
                                                                            alertDialog.setTitle(label);
                                                                            Bitmap bitmap = ((BitmapDrawable) ic2).getBitmap();
                                                                            Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
                                                                            alertDialog.setIcon(d);
                                                                        }
                                                                        alertDialog.show();

                                                                    } catch (PackageManager.NameNotFoundException e) {
                                                                        e.printStackTrace();
                                                                    } catch (NullPointerException e) {
                                                                        System.out.println("Deu Merda");
                                                                    }
                                                                    break;


                                                                case 1: // ABRE APP NA PLAY STORE
                                                                    String url = "https://play.google.com/store/apps/details?id=" + ameaca + "&hl=pt_BR";
                                                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                                                    i.setData(Uri.parse(url));
                                                                    startActivity(i);

                                                                    break;
                                                                case 2: //ASSINATURA

                                                                    final AlertDialog.Builder assinatura = new AlertDialog.Builder(MainActivity.this);
                                                                    assinatura.setTitle("Assinatura");
                                                                    assinatura.setIcon(R.drawable.book);


                                                                    Signature[] sigs = new Signature[0];
                                                                    try {
                                                                        sigs = getPackageManager().getPackageInfo(ameaca, PackageManager.GET_SIGNATURES).signatures;
                                                                        for (Signature sig : sigs) {

                                                                            String permissao = sig.toString();
                                                                            assinatura.setMessage(permissao);

                                                                        }
                                                                        assinatura.create().show();
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                    break;
                                                                case 3:
                                                                    break;
                                                            }
                                                        }
                                                    });
                                            detalhes.create().show();


                                            break;

                                        case 3: //continuar monitorando
                                            // Toast.makeText(MainActivity.this, "clicked 4", Toast.LENGTH_SHORT).show();

                                            break;
                                    }
                                }

                            });


                    builder.create().show();

                    //SISTEMA SEGURO

                } else if (teste2 == false) {
                    out.setText(outp);
                    Log.d("Output", outp);
                    AlertDialog.Builder alerta2 = new AlertDialog.Builder(MainActivity.this);
                    alerta2.setTitle("Sistema Seguro");
                    alerta2.setIcon(R.drawable.check);
                    showNotificationOK();  // NOTIFICAÇÃO DE OK

                    alerta2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    alerta2.create().show();
                }
            }
        });
    }

        public void startSecondActivity (View view){
            Intent secondActivity = new Intent(this, SecondActivity.class);
            startActivity(secondActivity);


    }

}