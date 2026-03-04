<template>
  <a-upload
    name="file"
    :show-upload-list="false"
    :custom-request="handleUpload"
    :before-upload="beforeUpload"
    :accept="accept"
  >
    <div
      v-if="fileUrl"
      class="file-preview"
    >
      <div
        v-if="isVideo"
        class="video-preview"
      >
        <div class="file-path">
          {{ fileUrl }}
        </div>
      </div>
      <div
        v-else
        class="video-preview"
      >
        <div class="file-path">
          {{ fileUrl }}
        </div>
      </div>
      <div class="file-mask">
        <span>更换文件</span>
      </div>
    </div>
    <div
      v-else
      class="upload-placeholder"
    >
      <UploadOutlined />
      <div style="margin-top: 8px">
        点击上传{{ type === 'video' ? '视频' : 'PDF' }}文件
      </div>
      <div style="font-size: 12px; color: #999; margin-top: 4px">
        支持{{ type === 'video' ? 'MP4' : 'PDF' }}格式，
        大小不超过{{ maxSize }}MB
      </div>
    </div>
  </a-upload>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { message } from 'ant-design-vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import axios from 'axios'

const props = defineProps({
  modelValue: {
    type: String,
    default: undefined
  },
  type: {
    type: String,
    default: 'video',
    validator: (value) => ['video', 'pdf'].includes(value)
  },
  maxSize: {
    type: Number,
    default: 100
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

const fileUrl = ref(props.modelValue || '')
const uploading = ref(false)

const accept = computed(() => {
  if (props.type === 'video') {
    return 'video/mp4,.mp4'
  } else {
    return '.pdf'
  }
})

const isVideo = computed(() => props.type === 'video')

watch(() => props.modelValue, (val) => {
  fileUrl.value = val || ''
})

const beforeUpload = (file) => {
  const maxSize = props.maxSize * 1024 * 1024
  if (file.size > maxSize) {
    message.error(`文件大小不能超过${props.maxSize}MB`)
    return false
  }

  if (props.type === 'video') {
    if (file.type !== 'video/mp4' && !file.name.match(/\.mp4$/i)) {
      message.error('只能上传 MP4 格式的视频文件')
      return false
    }
  } else {
    if (file.type !== 'application/pdf' && !file.name.match(/\.pdf$/i)) {
      message.error('只能上传 PDF 文件')
      return false
    }
  }

  return true
}

const handleUpload = async ({ file, onSuccess, onError }) => {
  uploading.value = true

  const formData = new FormData()
  formData.append('file', file)

  try {
    const token = localStorage.getItem('token')
    const { data } = await axios.post('/admin/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: `Bearer ${token}`
      },
      onUploadProgress: (progressEvent) => {
        const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        message.loading({ content: `上传中 ${percentCompleted}%`, key: 'upload', duration: 0 })
      }
    })

    message.destroy('upload')

    if (data.code === 200) {
      fileUrl.value = data.data.url
      emit('update:modelValue', data.data.url)
      emit('change', data.data)
      message.success('上传成功')
      onSuccess()
    } else {
      message.error(data.message || '上传失败')
      onError(new Error(data.message))
    }
  } catch (e) {
    message.destroy('upload')
    message.error('上传失败：' + e.message)
    onError(e)
  } finally {
    uploading.value = false
  }
}
</script>

<style scoped>
.file-preview {
  width: 100%;
  max-width: 400px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  padding: 8px;
}

.file-preview:hover .file-mask {
  opacity: 1;
}

.file-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  opacity: 0;
  transition: opacity 0.3s;
}

.upload-placeholder {
  width: 100%;
  max-width: 400px;
  height: 150px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #666;
  transition: border-color 0.3s;
}

.upload-placeholder:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.video-preview {
  padding: 12px;
}

.file-path {
  word-break: break-all;
  color: #666;
  font-size: 13px;
  line-height: 1.5;
}
</style>
