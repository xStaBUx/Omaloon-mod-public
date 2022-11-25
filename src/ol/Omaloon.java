package ol;

import arc.*;
import arc.func.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mma.*;
import mma.utils.*;
import ol.content.*;
import ol.graphics.*;
import ol.ui.*;
import ol.ui.dialogs.*;

import java.util.*;

import static arc.Core.*;
import static mindustry.Vars.*;

public class Omaloon extends MMAMod{
    public Omaloon(){
        OlVars.load();

        Events.on(FileTreeInitEvent.class, e -> app.post(OlSounds::load));
        Log.info("Loaded Omaloon constructor.");
        Events.on(FileTreeInitEvent.class, e -> Core.app.post(OlShaders::load));
    }

    @Override
    public void init(){
        super.init();
        ManyPlanetSystems.init();
        LoadedMod mod = ModVars.modInfo;
        if(headless) return;
        //forom Betamindy by sk7725
        mod.meta.displayName = bundle.get("mod." + mod.meta.name+".name");
        mod.meta.description = bundle.get("mod.ol.description") + "\n\n" + bundle.get("mod.ol.musics");
        mod.meta.author = bundle.get("mod.ol.author") + "\n\n" + bundle.get("mod.ol.contributors");
        //Random subtitles vote
        String [] r = {
        bundle.get("mod.ol.subtitle1"),
        bundle.get("mod.ol.subtitle2"),
        bundle.get("mod.ol.subtitle3"),
        bundle.get("mod.ol.subtitle4")
        };
        Random rand = new Random();
        String mogus = String.valueOf(
        r[rand.nextInt(3)]
        );
        mod.meta.subtitle = "[#7f7f7f]" + "v" + mod.meta.version + "[]" + "\n" + mogus;
        Events.on(ClientLoadEvent.class, e -> {
            loadSettings();
            OlSettings.init();
            app.post(() -> app.post(() -> {
                if(!settings.getBool("mod.ol.show", false)){
                    new OlDisclaimer().show();
                }
            }));
        });
        if(!mobile){
            Events.on(ClientLoadEvent.class, e -> {
                Table t = new Table();
                t.margin(4f);
                t.labelWrap("[#87ceeb]" + "Omaloon" + "[]" + "[#7f7f7f]" + " v" + mod.meta.version + "[]" + "\n" + mogus);
                t.pack();
                scene.add(t.visible(() -> state.isMenu()));
            });
        }
    }

    @Override
    protected void modContent(Content content){
        super.modContent(content);

        if(content instanceof MappableContent){
//            OlContentRegions.loadRegions((MappableContent)content);
        }
    }

    void loadSettings(){
        ui.settings.addCategory("@mod.ol.omaloon-settings", OlVars.fullName("settings-icon"), t -> {
            t.checkPref("mod.ol.show", false);
            t.checkPref("mod.ol.check", true);
            t.fill(c -> c.bottom().right().button(Icon.discord, new ImageButton.ImageButtonStyle(), new OlDiscordLink()::show).marginTop(9f).marginLeft(10f).tooltip(bundle.get("setting.ol.discord-join")).size(84, 45).name("discord"));
        });
    }

    @Override
    public void loadContent(){
        ModVars.modLog("Loading some content.");
        super.loadContent();
    }
}
