/*
 * Copyright (C) 2017  Hexeption (Keir Davis)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;

public class Login extends Plugin {

    private static final Logger logger = Logger.getLogger("Minecraft");

    private static HashMap<String, LPlayer> players = new HashMap<>();

    public static HashMap<String, LPlayer> getPlayers() {

        return players;
    }

    public static void save() {

        File folder = new File("Accounts");
        if ((!folder.exists()) && (!folder.mkdir())) {
            logger.info("[Login] Failed to create the 'Account' Folder");
        }
        File file = new File("Accounts/Accounts.hex");
        if (file.exists()) file.delete();

        FileWriter fileWriter;
        try {
            if (!file.createNewFile()) {
                logger.info("[Login] Save Error - Create File");
            }
            fileWriter = new FileWriter(file);
            if (!players.isEmpty()) {
                for (String name : players.keySet()) {
                    LPlayer lPlayer = players.get(name);
                    String account = name + ":A:" + lPlayer.password + ":A:" + lPlayer.items;

                    fileWriter.flush();
                    fileWriter.write(account + "\r\n");
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            logger.info("[Login] Error Saving Account");
        }
    }

    @Override
    public void enable() {

        logger.info("[Login] Enabled");

        logger.info("[Login] Loading Accounts");

        File file = new File("Accounts/Accounts.hex");
        if (file.exists()) {
            Scanner reader = null;
            try {
                reader = new Scanner(file);
                while (reader.hasNextLine()) {
                    String line = reader.nextLine();
                    if (line.contains(":A:")) {
                        String[] split = line.split(":A:");
                        if (split.length >= 2) {
                            LPlayer lPlayer = new LPlayer(split[1].trim());
                            if ((split.length == 3) && (split[2].length() > 4)) {
                                lPlayer.items = split[2];
                            }
                            players.put(split[0].trim(), lPlayer);
                        }
                    }
                }
                logger.info("[Login] Load Success");
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            logger.info("[Login] Failed to load 'Accounts.hex'");
        }

    }

    @Override
    public void disable() {

        logger.info("[Login] Disabled");

        save();

        for (Player player : etc.getServer().getPlayerList()) {
            player.kick("§c[Login] Reloading");
        }
    }

    @Override
    public void initialize() {

        LoginListerner listerner = new LoginListerner();
        PluginLoader loader = etc.getLoader();

        loader.addListener(PluginLoader.Hook.COMMAND, listerner, this, PluginListener.Priority.HIGH);
        loader.addListener(PluginLoader.Hook.LOGIN, listerner, this, PluginListener.Priority.HIGH);
        loader.addListener(PluginLoader.Hook.DISCONNECT, listerner, this, PluginListener.Priority.HIGH);
        loader.addListener(PluginLoader.Hook.LOGINCHECK, listerner, this, PluginListener.Priority.HIGH);
        loader.addListener(PluginLoader.Hook.BLOCK_DESTROYED, listerner, this, PluginListener.Priority.HIGH);
        loader.addListener(PluginLoader.Hook.BLOCK_CREATED, listerner, this, PluginListener.Priority.HIGH);
        loader.addListener(PluginLoader.Hook.OPEN_INVENTORY, listerner, this, PluginListener.Priority.HIGH);
        loader.addListener(PluginLoader.Hook.PLAYER_MOVE, listerner, this, PluginListener.Priority.HIGH);
        loader.addListener(PluginLoader.Hook.CHAT, listerner, this, PluginListener.Priority.HIGH);

    }
}
