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
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class represents the main (and only) view of the visualizer application, including the logic behind it.
 *
 * @author Šimon Vacek
 */
@Route("")
public class MainView extends VerticalLayout {
    public MainView() {
        setAlignItems(Alignment.CENTER);
        String serverName = getServerName();
        if (serverName == null) {
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

        wrapper.setWidth(600.0f, Unit.PIXELS);
        wrapper.setAlignItems(Alignment.CENTER);
        wrapper.add(new H1("List of addresses contacted:"));

        HorizontalLayout rowInList = new HorizontalLayout();
        rowInList.getThemeList().add("spacing-xl");

        for (var address : clientAddresses) {
            Div newDiv = new Div(new Label(serverName + " → " + address));
            newDiv.addClickListener(e -> {
                createDetail(address, detail);
            });
            if (rowInList.getComponentCount() == 2) {
                wrapper.add(rowInList);
                rowInList = new HorizontalLayout();
                rowInList.getThemeList().add("spacing-xl");
            }
            rowInList.add(newDiv);
        }

        wrapper.add(rowInList, detail);
        add(wrapper);
    }

    /**
     * Finds and returns unique of all clients contacted by the server.
     *
     * @return HashSet of all unique addresses.
     */
    private HashSet<String> getClientAddresses() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://gist.githubusercontent.com/hajda14/8da0b313b0503b0faee7a8d7fe63d9ca/raw/2eb3eb138e8307af00c0c64f20c97e3c802d54a2/testlog").openStream()));
            String inputLine;
            HashSet<String> clientAddresses = new HashSet<>();
            while ((inputLine = in.readLine()) != null) {
                // client addresses are mentioned only sent logs
                if (inputLine.contains(" SENT:")) {
                    String resource = inputLine.substring(inputLine.lastIndexOf("http"));
                    // resource now contains https://address/path, so the address will be third item in the split list
                    clientAddresses.add(resource.split("/")[2]);
                }
            }
            in.close();
            return clientAddresses;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Finds and returns the server name which is the subject of the log.
     *
     * @return Name of the server mentioned in the log.
     */
    private String getServerName() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://gist.githubusercontent.com/hajda14/8da0b313b0503b0faee7a8d7fe63d9ca/raw/2eb3eb138e8307af00c0c64f20c97e3c802d54a2/testlog").openStream()));
            String inputLine = in.readLine();
            in.close();
            // since the log is formatted as "DATE TIME INFO SERVER_NAME ...", this "hack" can be used
            return inputLine.split(" ")[4];
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Fills the detailed section with information of communication.
     * @param clientAddress Address of a client contacted by server.
     * @param detail Layout Vaadin component to be filled with data.
     */
    private void createDetail(String clientAddress, VerticalLayout detail) {
        detail.removeAll();
        detail.add(new H1("Detail of: " + clientAddress));
        detail.setVisible(true);
        detail.setAlignItems(Alignment.CENTER);

        // gets all the data containing history of communication
        ArrayList<String> data = getDetailedCommunication(clientAddress);
        if(data == null) {
            detail.add(new Div(new Paragraph("There was an error loading the log from: https://gist.githubusercontent.com/hajda14/8da0b313b0503b0faee7a8d7fe63d9ca/raw/2eb3eb138e8307af00c0c64f20c97e3c802d54a2/testlog")));
            return;
        }

        boolean isItResponse = false;
        for (String item : data) {
            // adds data items to the detail component in containers properly aligned to the left or right
            HorizontalLayout container = new HorizontalLayout(new Label(item));
            container.setWidthFull();
            detail.add(container);
            if (!isItResponse)
                container.setJustifyContentMode(JustifyContentMode.START);
            else
                container.setJustifyContentMode(JustifyContentMode.END);

            isItResponse = !isItResponse;
        }
    }

    /**
     * Finds and extracts all important parts communication regarding given client address contacted by the server.
     * @param clientAddress Address of a client contacted by the server.
     * @return ArrayList, where odd items are requests and even are responses.
     */
    private ArrayList<String> getDetailedCommunication(String clientAddress) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://gist.githubusercontent.com/hajda14/8da0b313b0503b0faee7a8d7fe63d9ca/raw/2eb3eb138e8307af00c0c64f20c97e3c802d54a2/testlog").openStream()));

            String inputLine;
            boolean isItResponse = false;
            ArrayList<String> data = new ArrayList<>();
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains(clientAddress)) {
                    String sentData = inputLine.substring(inputLine.indexOf("SENT:"));
                    data.add(sentData.replaceAll("https?://" + clientAddress + "/", ""));
                    isItResponse = true;
                } else if (isItResponse) {
                    data.add(inputLine.substring(inputLine.indexOf("RECEIVED:")));
                    isItResponse = false;
                }
            }
            return data;
        } catch (IOException e) {
            return null;
        }
    }

}
