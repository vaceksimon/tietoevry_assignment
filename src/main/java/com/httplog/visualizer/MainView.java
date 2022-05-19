package com.httplog.visualizer;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

@Route("")
public class MainView extends VerticalLayout {
    public MainView() {
        var clientAddesses = getClientAddresses();
        if (clientAddesses == null) {
            add(new Div(new Paragraph("There was an error loading the log from: https://gist.githubusercontent.com/hajda14/8da0b313b0503b0faee7a8d7fe63d9ca/raw/2eb3eb138e8307af00c0c64f20c97e3c802d54a2/testlog")));
            return;
        }

        clientAddesses.forEach(address -> {
            Div newDiv = new Div(new Label(address));
            newDiv.addClickListener(e -> {
                System.out.println("detailed view");
            });
            add(newDiv);
        });

    }

    private HashSet<String> getClientAddresses() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://gist.githubusercontent.com/hajda14/8da0b313b0503b0faee7a8d7fe63d9ca/raw/2eb3eb138e8307af00c0c64f20c97e3c802d54a2/testlog").openStream()));
            String inputLine;
            HashSet<String> clientAddresses = new HashSet<>();
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains(" SENT:")) {
                    String resource = inputLine.substring(inputLine.lastIndexOf("https://"));
                    clientAddresses.add(resource.split("/")[2]);
                }
            }
            in.close();
            return clientAddresses;
        } catch (IOException e) {
        return null;
        }
    }
}
