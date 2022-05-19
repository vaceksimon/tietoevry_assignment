package com.httplog.visualizer;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;

@Route("")
public class MainView extends VerticalLayout {
    public MainView() {
        setAlignItems(Alignment.CENTER);
        String serverName = getServerName();
        if(serverName == null) {
            add(new Div(new Paragraph("There was an error loading the log from: https://gist.githubusercontent.com/hajda14/8da0b313b0503b0faee7a8d7fe63d9ca/raw/2eb3eb138e8307af00c0c64f20c97e3c802d54a2/testlog")));
            return;
        }

        var clientAddresses = getClientAddresses();
        if (clientAddresses == null) {
            add(new Div(new Paragraph("There was an error loading the log from: https://gist.githubusercontent.com/hajda14/8da0b313b0503b0faee7a8d7fe63d9ca/raw/2eb3eb138e8307af00c0c64f20c97e3c802d54a2/testlog")));
            return;
        }
        VerticalLayout wrapper = new VerticalLayout();

        VerticalLayout detail = new VerticalLayout();
        detail.setVisible(false);

        wrapper.setWidth(900.0f, Unit.PIXELS);
        wrapper.setMaxWidth(900.0f, Unit.PIXELS);
        wrapper.setAlignItems(Alignment.CENTER);
        wrapper.add(new H1("List of clients contacted by the server:"));

        HorizontalLayout rowInList = new HorizontalLayout();
        rowInList.getThemeList().add("spacing-xl");

        for (var address : clientAddresses) {
            Div newDiv = new Div(new Label(serverName + " → " + address));
            newDiv.addClickListener(e -> {
                createDetail(address, detail);
            });
            if(rowInList.getComponentCount() == 2) {
                wrapper.add(rowInList);
                rowInList = new HorizontalLayout();
                rowInList.getThemeList().add("spacing-xl");
            }
            rowInList.add(newDiv);
        }

        wrapper.add(rowInList, detail);
        add(wrapper);
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

    private String getServerName() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://gist.githubusercontent.com/hajda14/8da0b313b0503b0faee7a8d7fe63d9ca/raw/2eb3eb138e8307af00c0c64f20c97e3c802d54a2/testlog").openStream()));
            String inputLine = in.readLine();
            in.close();
            return inputLine.split(" ")[4];
        } catch (IOException e) {
            return null;
        }
    }

    private void createDetail(String clientAddress, VerticalLayout detail) {
        detail.removeAll();
        detail.add(new H1("Detail of: " + clientAddress));
        detail.setVisible(true);
        detail.setAlignItems(Alignment.CENTER);
    }

}
