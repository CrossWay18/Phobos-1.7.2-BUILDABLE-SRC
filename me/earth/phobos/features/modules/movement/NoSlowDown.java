/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiChat
 *  net.minecraft.client.gui.GuiIngameMenu
 *  net.minecraft.client.gui.GuiOptions
 *  net.minecraft.client.gui.GuiScreenOptionsSounds
 *  net.minecraft.client.gui.GuiVideoSettings
 *  net.minecraft.client.gui.inventory.GuiContainer
 *  net.minecraft.client.settings.KeyBinding
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketPlayer
 *  net.minecraft.network.play.client.CPacketPlayerDigging
 *  net.minecraft.network.play.client.CPacketPlayerDigging$Action
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.client.event.InputUpdateEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.input.Keyboard
 */
package me.earth.phobos.features.modules.movement;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.KeyEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.movement.Flight;
import me.earth.phobos.features.modules.movement.Phase;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoSlowDown
extends Module {
    public Setting<Boolean> guiMove = this.register(new Setting<Boolean>("GuiMove", true));
    public Setting<Boolean> noSlow = this.register(new Setting<Boolean>("NoSlow", true));
    public Setting<Boolean> soulSand = this.register(new Setting<Boolean>("SoulSand", true));
    public Setting<Boolean> strict = this.register(new Setting<Boolean>("Strict", false));
    public Setting<Boolean> webs = this.register(new Setting<Boolean>("Webs", false));
    public final Setting<Double> webHorizontalFactor = this.register(new Setting<Double>("WebHSpeed", 2.0, 0.0, 100.0));
    public final Setting<Double> webVerticalFactor = this.register(new Setting<Double>("WebVSpeed", 2.0, 0.0, 100.0));
    private static NoSlowDown INSTANCE = new NoSlowDown();
    private static KeyBinding[] keys = new KeyBinding[]{NoSlowDown.mc.field_71474_y.field_74351_w, NoSlowDown.mc.field_71474_y.field_74368_y, NoSlowDown.mc.field_71474_y.field_74370_x, NoSlowDown.mc.field_71474_y.field_74366_z, NoSlowDown.mc.field_71474_y.field_74314_A, NoSlowDown.mc.field_71474_y.field_151444_V};

    public NoSlowDown() {
        super("NoSlowDown", "Prevents you from getting slowed down.", Module.Category.MOVEMENT, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    public static NoSlowDown getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoSlowDown();
        }
        return INSTANCE;
    }

    @Override
    public void onUpdate() {
        if (this.guiMove.getValue().booleanValue()) {
            if (NoSlowDown.mc.field_71462_r instanceof GuiOptions || NoSlowDown.mc.field_71462_r instanceof GuiVideoSettings || NoSlowDown.mc.field_71462_r instanceof GuiScreenOptionsSounds || NoSlowDown.mc.field_71462_r instanceof GuiContainer || NoSlowDown.mc.field_71462_r instanceof GuiIngameMenu) {
                for (KeyBinding bind : keys) {
                    KeyBinding.func_74510_a((int)bind.func_151463_i(), (boolean)Keyboard.isKeyDown((int)bind.func_151463_i()));
                }
            } else if (NoSlowDown.mc.field_71462_r == null) {
                for (KeyBinding bind : keys) {
                    if (Keyboard.isKeyDown((int)bind.func_151463_i())) continue;
                    KeyBinding.func_74510_a((int)bind.func_151463_i(), (boolean)false);
                }
            }
        }
        if (this.webs.getValue().booleanValue() && Phobos.moduleManager.getModuleByClass(Flight.class).isDisabled() && Phobos.moduleManager.getModuleByClass(Phase.class).isDisabled() && NoSlowDown.mc.field_71439_g.field_70134_J) {
            NoSlowDown.mc.field_71439_g.field_70159_w *= this.webHorizontalFactor.getValue().doubleValue();
            NoSlowDown.mc.field_71439_g.field_70179_y *= this.webHorizontalFactor.getValue().doubleValue();
            NoSlowDown.mc.field_71439_g.field_70181_x *= this.webVerticalFactor.getValue().doubleValue();
        }
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        if (this.noSlow.getValue().booleanValue() && NoSlowDown.mc.field_71439_g.func_184587_cr() && !NoSlowDown.mc.field_71439_g.func_184218_aH()) {
            event.getMovementInput().field_78902_a *= 5.0f;
            event.getMovementInput().field_192832_b *= 5.0f;
        }
    }

    @SubscribeEvent
    public void onKeyEvent(KeyEvent event) {
        if (this.guiMove.getValue().booleanValue() && event.getStage() == 0 && !(NoSlowDown.mc.field_71462_r instanceof GuiChat)) {
            event.info = event.pressed;
        }
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && this.strict.getValue().booleanValue() && this.noSlow.getValue().booleanValue() && NoSlowDown.mc.field_71439_g.func_184587_cr() && !NoSlowDown.mc.field_71439_g.func_184218_aH()) {
            NoSlowDown.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(NoSlowDown.mc.field_71439_g.field_70165_t), Math.floor(NoSlowDown.mc.field_71439_g.field_70163_u), Math.floor(NoSlowDown.mc.field_71439_g.field_70161_v)), EnumFacing.DOWN));
        }
    }
}

