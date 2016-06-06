package org.wolframmc;

/**
 * Created by addisonparkhurst on 6/6/16.
 */

import com.wolfram.alpha.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;


public class Main extends JavaPlugin {

    public WAEngine wa;
    public String appid;
    public WAQuery query;
    public WAQueryResult result;

    @Override
    public void onEnable() {
        this.getLogger().info("[WA] Configuring WolframMC...");
        this.saveDefaultConfig();
        getConfig().addDefault("API_KEY", "TGGP29-R25L4UQYEW");
        this.setupWA();
        this.getLogger().info("[WA] Done...");
    }

    public void setupWA() {
        this.wa = new WAEngine();
        this.appid = getConfig().get("API_KEY").toString();
        this.query = wa.createQuery();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String request = args.toString();
        request = request.replace("[", "");
        request = request.replace("]", "");
        request = request.replace(",", "");
        if(cmd.getName().equalsIgnoreCase("wa")) {
            if(this.appid.equals("API")) {
                sender.sendMessage("No API key has been set");
            } else {
                sender.sendMessage("Completing request for " + request);
                try {
                    this.query.setInput(request);
                    this.result = this.wa.performQuery(this.query);
                    if(!result.isSuccess()) {
                        sender.sendMessage("No results available");
                    } else {
                        WAPod pod = result.getPods()[1];
                        for(WASubpod subpod : pod.getSubpods()) {
                            if(subpod instanceof WAPlainText) {
                                sender.sendMessage(((WAPlainText) subpod).getText());
                            } else {
                                sender.sendMessage("No readable results could be found");
                            }
                        }
                    }
                } catch (WAException e) {
                    sender.sendMessage("There was an error completing your request...");
                    sender.sendMessage(e.getMessage());
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Shutting down...");
        this.saveDefaultConfig();
    }
}
