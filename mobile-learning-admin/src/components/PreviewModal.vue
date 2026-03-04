<template>
  <a-modal
    v-model:open="visible"
    :title="title"
    width="90%"
    style="top: 20px"
    :footer="null"
    @cancel="handleClose"
  >
    <div class="preview-container">
      <div
        v-if="type === 'video'"
        class="video-container"
      >
        <video
          :src="url"
          controls
          style="width: 100%; max-height: calc(100vh - 200px)"
        />
      </div>
      <div
        v-else-if="type === 'pdf'"
        class="pdf-container"
      >
        <VuePdfEmbed :pdf="url" />
      </div>
      <div
        v-else
        class="empty-container"
      >
        <a-empty description="暂无预览" />
      </div>
    </div>
  </a-modal>
</template>

<script setup>
import { ref, watch } from 'vue'
import VuePdfEmbed from 'vue3-pdf-app'

const props = defineProps({
  open: {
    type: Boolean,
    default: false
  },
  url: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: 'video',
    validator: (value) => ['video', 'pdf'].includes(value)
  },
  title: {
    type: String,
    default: '文件预览'
  }
})

const emit = defineEmits(['update:open'])

const visible = ref(props.open)

watch(() => props.open, (val) => {
  visible.value = val
})

const handleClose = () => {
  visible.value = false
  emit('update:open', false)
}
</script>

<style scoped>
.preview-container {
  min-height: 400px;
}

.video-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.pdf-container {
  height: calc(100vh - 200px);
  overflow: auto;
}

.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}
</style>
