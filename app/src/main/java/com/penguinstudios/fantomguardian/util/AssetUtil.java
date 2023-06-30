package com.penguinstudios.fantomguardian.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AssetUtil {

    //Loads the file into the static variable.
    //This is because AS IDE does not accept the length of the binary code in multiple lines
    //The text must be in 1 line
    public static String getContractBin(Context context, String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = context.getAssets().open(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
            return sb.toString();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No txt file in assets folder found for: " + fileName);
        } catch (IOException e) {
            throw new IOException("Buffered reader failed to read.");
        }
    }
}
