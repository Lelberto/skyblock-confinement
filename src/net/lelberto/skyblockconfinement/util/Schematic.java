package net.lelberto.skyblockconfinement.util;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.lelberto.skyblockconfinement.SkyblockException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Schematic {

    public static Schematic load(File schemFile) throws SkyblockException {
        try {
            ClipboardFormat format = ClipboardFormats.findByFile(schemFile);
            ClipboardReader reader = format.getReader(new FileInputStream(schemFile));
            return new Schematic(reader.read());
        } catch (IOException ex) {
            throw new SkyblockException("Could not load the schematic \"" + schemFile.getAbsolutePath() + "\"", ex);
        }
    }

    private final Clipboard clipboard;

    private Schematic(Clipboard clipboard) {
        this.clipboard = clipboard;
    }

    public void build(Location location) {
        com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(location.getWorld());
        EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, -1);

        Operation op = new ClipboardHolder(clipboard).createPaste(session).to(BlockVector3.at(location.getX(), location.getY(), location.getZ())).ignoreAirBlocks(true).build();
        try {
            Operations.complete(op);
        } catch (WorldEditException ex) {
            ex.printStackTrace();
        }
        session.flushSession();
    }
}
