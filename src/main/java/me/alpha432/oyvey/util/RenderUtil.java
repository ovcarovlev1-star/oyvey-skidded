package me.alpha432.oyvey.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.awt.Color;

public class RenderUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    /**
     * Рисует линию в 3D мире.
     * Используется в модуле Tracers.
     */
    public static void drawLine(float x1, float y1, float z1, float x2, float y2, float z2, Color color, float width) {
        // Настройка GL состояний
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram); // Важно для 1.21

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);

        bufferBuilder.vertex(x1, y1, z1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        bufferBuilder.vertex(x2, y2, z2).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());

        // В 1.21 отрисовка происходит через BuiltBuffer
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    /**
     * Рисует вертикальную линию (Stem) для Tracers.
     */
    public static void drawVerticalLine(double x, double y, double z, float height, Color color) {
        drawLine((float)x, (float)y, (float)z, (float)x, (float)(y + height), (float)z, color, 1.0f);
    }

    /**
     * Вспомогательный метод для получения MatrixStack (если понадобится для коробок)
     */
    public static MatrixStack getMatrices() {
        MatrixStack matrices = new MatrixStack();
        matrices.multiply(RotationAxis.POSITIVE_X.getDegreesQuaternion(mc.getEntityRenderDispatcher().camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.getDegreesQuaternion(mc.getEntityRenderDispatcher().camera.getYaw() + 180.0F));
        return matrices;
    }
}
