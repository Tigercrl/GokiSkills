package io.github.tigercrl.gokiskills.client.gui;

public class SkillResource<T> {
    private final T defaultItem;
    private final T hoverItem;
    private final T maxLevelItem;
    private final T operationItem; // shift or ctrl key down
    private final T operationHoverItem;

    public SkillResource(T defaultItem, T hoverItem, T maxLevelItem, T operationItem, T operationHoverItem) {
        this.defaultItem = defaultItem;
        this.hoverItem = hoverItem;
        this.maxLevelItem = maxLevelItem;
        this.operationItem = operationItem;
        this.operationHoverItem = operationHoverItem;
    }

    public T getDefaultItem() {
        return defaultItem;
    }

    public T getHoverItem() {
        return hoverItem;
    }

    public T getMaxLevelItem() {
        return maxLevelItem;
    }

    public T getOperationItem() {
        return operationItem;
    }

    public T getOperationHoverItem() {
        return operationHoverItem;
    }

    public T getItem(boolean hover, boolean maxLevel, boolean operation) {
        if (operation) return hover ? operationHoverItem : operationItem;
        if (maxLevel) return maxLevelItem;
        return hover ? hoverItem : defaultItem;
    }

    public static class Builder<T> {
        private T defaultItem;
        private T hoverItem;
        private T maxLevelItem;
        private T operationItem;
        private T operationHoverItem;
        private int textureWidth;
        private int textureHeight;

        public Builder<T> setDefaultItem(T defaultItem) {
            this.defaultItem = defaultItem;
            return this;
        }

        public Builder<T> setHoverItem(T hoverItem) {
            this.hoverItem = hoverItem;
            return this;
        }

        public Builder<T> setMaxLevelItem(T maxLevelItem) {
            this.maxLevelItem = maxLevelItem;
            return this;
        }

        public Builder<T> setOperationItem(T operationItem) {
            this.operationItem = operationItem;
            return this;
        }

        public Builder<T> setOperationHoverItem(T operationHoverItem) {
            this.operationHoverItem = operationHoverItem;
            return this;
        }

        public Builder<T> setTextureSize(int textureWidth, int textureHeight) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        public Builder<T> setTextureSize(int textureSize) {
            this.textureWidth = textureSize;
            this.textureHeight = textureSize;
            return this;
        }

        public Builder<T> setTextureWidth(int textureWidth) {
            this.textureWidth = textureWidth;
            return this;
        }

        public Builder<T> setTextureHeight(int textureHeight) {
            this.textureHeight = textureHeight;
            return this;
        }

        public SkillResource<T> build() {
            if (defaultItem == null) throw new IllegalStateException("Default image must be set");
            if (textureWidth <= 0) throw new IllegalStateException("Texture width must be set and greater than 0");
            if (textureHeight <= 0) throw new IllegalStateException("Texture height must be set and greater than 0");
            return new SkillResource<>(
                    defaultItem,
                    hoverItem == null ? defaultItem : hoverItem,
                    maxLevelItem == null ? defaultItem : maxLevelItem,
                    operationItem == null ? defaultItem : operationItem,
                    operationHoverItem == null ? (operationItem == null ? defaultItem : operationItem) : operationHoverItem
            );
        }
    }
}
