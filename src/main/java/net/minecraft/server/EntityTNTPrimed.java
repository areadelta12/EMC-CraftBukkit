package net.minecraft.server;

import org.bukkit.event.entity.ExplosionPrimeEvent; // CraftBukkit

public class EntityTNTPrimed extends Entity {

    public int fuseTicks;
    private EntityLiving source;
    public float yield = 4; // CraftBukkit - add field
    public boolean isIncendiary = false; // CraftBukkit - add field
    public org.bukkit.Location sourceLoc; // Spigot

    // Spigot start
    public EntityTNTPrimed(World world) {
        this(null, world);
    }
    public EntityTNTPrimed(org.bukkit.Location loc, World world) {
    // Spigot end
        super(world);
        sourceLoc = loc; // Spigot
        this.k = true;
        this.a(0.98F, 0.98F);
        this.height = this.length / 2.0F;
    }

    public EntityTNTPrimed(org.bukkit.Location loc, World world, double d0, double d1, double d2, EntityLiving entityliving) { // Spigot
        this(loc, world); // Spigot
        this.setPosition(d0, d1, d2);
        float f = (float) (Math.random() * 3.1415927410125732D * 2.0D);

        this.motX = (double) (-((float) Math.sin((double) f)) * 0.02F);
        this.motY = 0.20000000298023224D;
        this.motZ = (double) (-((float) Math.cos((double) f)) * 0.02F);
        this.fuseTicks = 80;
        this.lastX = d0;
        this.lastY = d1;
        this.lastZ = d2;
        this.source = entityliving;
    }

    protected void c() {}

    protected boolean g_() {
        return false;
    }

    public boolean R() {
        return !this.dead;
    }

    public void h() {
        if (world.spigotConfig.currentPrimedTnt++ > world.spigotConfig.maxTntTicksPerTick) { return; } // Spigot
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        this.motY -= 0.03999999910593033D;
        this.move(this.motX, this.motY, this.motZ);
        this.motX *= 0.9800000190734863D;
        this.motY *= 0.9800000190734863D;
        this.motZ *= 0.9800000190734863D;
        if (this.onGround) {
            this.motX *= 0.699999988079071D;
            this.motZ *= 0.699999988079071D;
            this.motY *= -0.5D;
        }

        if (this.fuseTicks-- <= 0) {
            // CraftBukkit start - Need to reverse the order of the explosion and the entity death so we have a location for the event
            if (!this.world.isStatic) {
                this.explode();
            }
            this.die();
            // CraftBukkit end
        } else {
            this.world.addParticle("smoke", this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
        }
    }

    private void explode() {
        // CraftBukkit start
        // float f = 4.0F;

        org.bukkit.craftbukkit.CraftServer server = this.world.getServer();

        ExplosionPrimeEvent event = new ExplosionPrimeEvent((org.bukkit.entity.Explosive) org.bukkit.craftbukkit.entity.CraftEntity.getEntity(server, this));
        server.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            // give 'this' instead of (Entity) null so we know what causes the damage
            this.world.createExplosion(this, this.locX, this.locY, this.locZ, event.getRadius(), event.getFire(), true);
        }
        // CraftBukkit end
    }

    protected void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("Fuse", (byte) this.fuseTicks);
        // Spigot start
        if (sourceLoc != null) {
            nbttagcompound.setInt("SourceLoc_x", sourceLoc.getBlockX());
            nbttagcompound.setInt("SourceLoc_y", sourceLoc.getBlockY());
            nbttagcompound.setInt("SourceLoc_z", sourceLoc.getBlockZ());
        }
        // Spigot end
    }

    protected void a(NBTTagCompound nbttagcompound) {
        this.fuseTicks = nbttagcompound.getByte("Fuse");
        // Spigot start
        if (nbttagcompound.hasKey("SourceLoc_x")) {
            int srcX = nbttagcompound.getInt("SourceLoc_x");
            int srcY = nbttagcompound.getInt("SourceLoc_y");
            int srcZ = nbttagcompound.getInt("SourceLoc_z");
            sourceLoc = new org.bukkit.Location(world.getWorld(), srcX, srcY, srcZ);
        }
        // Spigot end
    }

    public EntityLiving getSource() {
        return this.source;
    }
}
