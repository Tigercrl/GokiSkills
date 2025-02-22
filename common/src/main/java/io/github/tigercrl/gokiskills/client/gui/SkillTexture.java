package io.github.tigercrl.gokiskills.client.gui;

import net.minecraft.resources.ResourceLocation;

public class SkillTexture extends SkillResource<ResourceLocation> {
    private final int textureWidth;
    private final int textureHeight;

    public SkillTexture(ResourceLocation defaultImage, ResourceLocation hoverImage, ResourceLocation maxLevelImage, ResourceLocation operationImage, ResourceLocation operationHoverImage, int textureWidth, int textureHeight) {
        super(defaultImage, hoverImage, maxLevelImage, operationImage, operationHoverImage);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public static class Builder {
        private ResourceLocation defaultImage;
        private ResourceLocation hoverImage;
        private ResourceLocation maxLevelImage;
        private ResourceLocation operationImage;
        private ResourceLocation operationHoverImage;
        private int textureWidth;
        private int textureHeight;

        public Builder setDefaultImage(ResourceLocation defaultImage) {
            this.defaultImage = defaultImage;
            return this;
        }

        public Builder setHoverImage(ResourceLocation hoverImage) {
            this.hoverImage = hoverImage;
            return this;
        }

        public Builder setMaxLevelImage(ResourceLocation maxLevelImage) {
            this.maxLevelImage = maxLevelImage;
            return this;
        }

        public Builder setOperationImage(ResourceLocation operationImage) {
            this.operationImage = operationImage;
            return this;
        }

        public Builder setOperationHoverImage(ResourceLocation operationHoverImage) {
            this.operationHoverImage = operationHoverImage;
            return this;
        }

        public Builder setTextureSize(int textureWidth, int textureHeight) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        public Builder setTextureSize(int textureSize) {
            this.textureWidth = textureSize;
            this.textureHeight = textureSize;
            return this;
        }

        public Builder setTextureWidth(int textureWidth) {
            this.textureWidth = textureWidth;
            return this;
        }

        public Builder setTextureHeight(int textureHeight) {
            this.textureHeight = textureHeight;
            return this;
        }

        public SkillTexture build() {
            if (defaultImage == null) throw new IllegalStateException("Default image must be set");
            if (textureWidth <= 0) throw new IllegalStateException("Texture width must be set and greater than 0");
            if (textureHeight <= 0) throw new IllegalStateException("Texture height must be set and greater than 0");
            return new SkillTexture(
                    defaultImage,
                    hoverImage == null ? defaultImage : hoverImage,
                    maxLevelImage == null ? defaultImage : maxLevelImage,
                    operationImage == null ? defaultImage : operationImage,
                    operationHoverImage == null ? (operationImage == null ? defaultImage : operationImage) : operationHoverImage,
                    textureWidth,
                    textureHeight
            );
        }
    }
}
