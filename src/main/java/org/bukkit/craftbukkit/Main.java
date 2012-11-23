package org.bukkit.craftbukkit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.server.MinecraftServer;

public class Main {
    public static boolean useJline = true;
    public static boolean useConsole = true;

    public static void main(String[] args) throws Exception {
        // Spigot Start
        File lock = new File( ".update-lock" );
        if ( !new File( "update-lock" ).exists() && !lock.exists()  && System.getProperty( "IReallyKnowWhatIAmDoingThisUpdate" ) == null )
        {
            System.err.println( "WARNING: This Minecraft update alters the way in which saved data is stored." );
            System.err.println( "Please ensure your server is in the correct online/offline mode state, as the changes made are PERMANENT" );
            System.err.println( "If you are running in offline mode, but your BungeeCord is in online mode, it is imperative that BungeeCord support is enabled in spigot.yml and BungeeCord's config.yml" );
            System.err.println( "By typing `yes` you acknowledge that you have taken the necessary backups and are aware of this conversion" );
            System.err.println( "Please type yes to continue starting the server. You have been warned :)" );
            System.err.println( "See http://www.spigotmc.org/wiki/uuid-conversion/ if you have any questions and remember BACKUP BACKUP BACKUP" );
            System.err.println( "=================================================================================" );
            System.err.println( "Starting server in 10 seconds" );
            lock.createNewFile();
            try
            {
                Thread.sleep( TimeUnit.SECONDS.toMillis( 10 ) );
            } catch ( InterruptedException ex )
            {
            }
        }

        System.err.println( "This Spigot build supports Minecraft clients both of versions 1.7.x and of 1.8.x.\n"
                + "*** It is imperative that backups be taken before running this build on your server! ***\n"
                + "Please report any such issues to http://www.spigotmc.org/, stating your client, server, and if applicable BungeeCord versions.\n"
                + "*** Any bug reports not running the very latest versions of these softwares will be ignored ***\n\n" );

        Enumeration<URL> resources = Main.class.getClassLoader().getResources( "META-INF/MANIFEST.MF" );
        while ( resources.hasMoreElements() )
        {
            Manifest manifest = new Manifest( resources.nextElement().openStream() );
            String ts = manifest.getMainAttributes().getValue( "Timestamp" );
            if ( ts != null && false) // EMC
            {
                Date buildDate = new SimpleDateFormat( "yyyyMMdd-hhmm" ).parse( ts );

                Calendar cal = Calendar.getInstance();
                cal.add( Calendar.DAY_OF_YEAR, -2 );
                if ( buildDate.before(cal.getTime() ) )
                {
                    System.err.println( "WARNING: This build is more than 2 days old and there are likely updates available!" );
                    System.err.println( "You will get no support with this build unless you update from http://ci.md-5.net/job/Spigot/" );
                    System.err.println( "The server will start in 10 seconds!" );
                    Thread.sleep( TimeUnit.SECONDS.toMillis( 10 ) );
                }
            }
        }
        // Spigot End
        // Todo: Installation script
        OptionParser parser = new OptionParser() {
            {
                acceptsAll(asList("?", "help"), "Show the help");

                acceptsAll(asList("c", "config"), "Properties file to use")
                        .withRequiredArg()
                        .ofType(File.class)
                        .defaultsTo(new File("server.properties"))
                        .describedAs("Properties file");

                acceptsAll(asList("P", "plugins"), "Plugin directory to use")
                        .withRequiredArg()
                        .ofType(File.class)
                        .defaultsTo(new File("plugins"))
                        .describedAs("Plugin directory");

                acceptsAll(asList("h", "host", "server-ip"), "Host to listen on")
                        .withRequiredArg()
                        .ofType(String.class)
                        .describedAs("Hostname or IP");

                acceptsAll(asList("W", "world-dir", "universe", "world-container"), "World container")
                        .withRequiredArg()
                        .ofType(File.class)
                        .describedAs("Directory containing worlds");

                acceptsAll(asList("w", "world", "level-name"), "World name")
                        .withRequiredArg()
                        .ofType(String.class)
                        .describedAs("World name");

                acceptsAll(asList("p", "port", "server-port"), "Port to listen on")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .describedAs("Port");

                acceptsAll(asList("o", "online-mode"), "Whether to use online authentication")
                        .withRequiredArg()
                        .ofType(Boolean.class)
                        .describedAs("Authentication");

                acceptsAll(asList("s", "size", "max-players"), "Maximum amount of players")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .describedAs("Server size");

                acceptsAll(asList("d", "date-format"), "Format of the date to display in the console (for log entries)")
                        .withRequiredArg()
                        .ofType(SimpleDateFormat.class)
                        .describedAs("Log date format");

                acceptsAll(asList("log-pattern"), "Specfies the log filename pattern")
                        .withRequiredArg()
                        .ofType(String.class)
                        .defaultsTo("server.log")
                        .describedAs("Log filename");

                acceptsAll(asList("log-limit"), "Limits the maximum size of the log file (0 = unlimited)")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .defaultsTo(0)
                        .describedAs("Max log size");

                acceptsAll(asList("log-count"), "Specified how many log files to cycle through")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .defaultsTo(1)
                        .describedAs("Log count");

                acceptsAll(asList("log-append"), "Whether to append to the log file")
                        .withRequiredArg()
                        .ofType(Boolean.class)
                        .defaultsTo(true)
                        .describedAs("Log append");

                acceptsAll(asList("log-strip-color"), "Strips color codes from log file");

                acceptsAll(asList("b", "bukkit-settings"), "File for bukkit settings")
                        .withRequiredArg()
                        .ofType(File.class)
                        .defaultsTo(new File("bukkit.yml"))
                        .describedAs("Yml file");

                acceptsAll(asList("C", "commands-settings"), "File for command settings")
                        .withRequiredArg()
                        .ofType(File.class)
                        .defaultsTo(new File("commands.yml"))
                        .describedAs("Yml file");

                acceptsAll(asList("nojline"), "Disables jline and emulates the vanilla console");

                acceptsAll(asList("noconsole"), "Disables the console");

                acceptsAll(asList("v", "version"), "Show the CraftBukkit Version");

                acceptsAll(asList("demo"), "Demo mode");
            }
        };

        OptionSet options = null;

        try {
            options = parser.parse(args);
        } catch (joptsimple.OptionException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage());
        }

        if ((options == null) || (options.has("?"))) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (options.has("v")) {
            System.out.println(CraftServer.class.getPackage().getImplementationVersion());
        } else {
            try {
                // This trick bypasses Maven Shade's clever rewriting of our getProperty call when using String literals
                String jline_UnsupportedTerminal = new String(new char[] {'j','l','i','n','e','.','U','n','s','u','p','p','o','r','t','e','d','T','e','r','m','i','n','a','l'});
                String jline_terminal = new String(new char[] {'j','l','i','n','e','.','t','e','r','m','i','n','a','l'});

                useJline = !(jline_UnsupportedTerminal).equals(System.getProperty(jline_terminal));

                if (options.has("nojline")) {
                    System.setProperty("user.language", "en");
                    useJline = false;
                }

                if (!useJline) {
                    // This ensures the terminal literal will always match the jline implementation
                    System.setProperty(jline.TerminalFactory.JLINE_TERMINAL, jline.UnsupportedTerminal.class.getName());
                }


                if (options.has("noconsole")) {
                    useConsole = false;
                }

                // Spigot Start
                int maxPermGen = 0; // In kb
                for ( String s : java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments() )
                {
                    if ( s.startsWith( "-XX:MaxPermSize" ) )
                    {
                        maxPermGen = Integer.parseInt( s.replaceAll( "[^\\d]", "" ) );
                        maxPermGen <<= 10 * ("kmg".indexOf( Character.toLowerCase( s.charAt( s.length() - 1 ) ) ) );
                    }
                }
                if ( Float.parseFloat( System.getProperty( "java.class.version" ) ) < 52 && maxPermGen < ( 128 << 10 ) ) // 128mb
                {
                    System.out.println( "Warning, your max perm gen size is not set or less than 128mb. It is recommended you restart Java with the following argument: -XX:MaxPermSize=128M" );
                    System.out.println( "Please see http://www.spigotmc.org/wiki/changing-permgen-size/ for more details and more in-depth instructions." );
                }
                // Spigot End
                MinecraftServer.main(options);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private static List<String> asList(String... params) {
        return Arrays.asList(params);
    }
}
