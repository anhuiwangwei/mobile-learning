<template>
  <div class="page-container">
    <a-card>
      <a-page-header
        title="章节管理"
        @back="() => router.push('/courses')"
      >
        <template #extra>
          <a-button
            type="primary"
            @click="handleAddChapter"
          >
            添加章节
          </a-button>
        </template>
      </a-page-header>

      <a-collapse v-model:active-key="activeChapters">
        <a-collapse-panel
          v-for="chapter in chapters"
          :key="chapter.id"
          :header="chapter.chapterName"
        >
          <template #extra>
            <a @click.stop="handleAddSection(chapter.id)">添加小节</a>
            <a-divider type="vertical" />
            <a @click.stop="handleEditChapter(chapter)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定删除该章节吗？"
              @confirm="handleDeleteChapter(chapter.id)"
            >
              <a
                style="color: red"
                @click.stop
              >删除</a>
            </a-popconfirm>
          </template>

          <a-table
            :columns="sectionColumns"
            :data-source="getSections(chapter.id)"
            :pagination="false"
            row-key="id"
            size="small"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'sectionType'">
                <span style="font-size: 16px; margin-right: 4px">
                  {{ record.sectionType === 'video' ? '📹' : record.sectionType === 'pdf' ? '📄' : '📝' }}
                </span>
                <a-tag :color="getSectionTypeColor(record.sectionType)">
                  {{ getSectionTypeText(record.sectionType) }}
                </a-tag>
              </template>
              <template v-if="column.key === 'isAllowSeek'">
                {{ record.isAllowSeek === 1 ? '允许' : '禁止' }}
              </template>
              <template v-if="column.key === 'isNoDrag'">
                <a-tag :color="record.isNoDrag === 1 ? 'orange' : 'default'">
                  {{ record.isNoDrag === 1 ? '禁止拖动' : '允许拖动' }}
                </a-tag>
              </template>
              <template v-if="column.key === 'action'">
                <a-space>
                  <a
                    v-if="record.sectionType !== 'exam'"
                    @click.stop="handlePreview(record)"
                  >预览</a>
                  <a @click.stop="handleEditSection(record)">编辑</a>
                  <a-popconfirm
                    title="确定删除该小节吗？"
                    @confirm="handleDeleteSection(record.id)"
                  >
                    <a style="color: red">删除</a>
                  </a-popconfirm>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-collapse-panel>
      </a-collapse>
    </a-card>

    <!-- 章节 Modal -->
    <a-modal
      v-model:open="chapterModalVisible"
      :title="isEditChapter ? '编辑章节' : '添加章节'"
      @ok="handleChapterSubmit"
    >
      <a-form
        :model="chapterForm"
        :label-col="{ span: 4 }"
      >
        <a-form-item
          label="章节名称"
          required
        >
          <a-input
            v-model:value="chapterForm.chapterName"
            placeholder="请输入章节名称"
          />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number
            v-model:value="chapterForm.chapterOrder"
            :min="1"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 小节 Modal -->
    <a-modal
      v-model:open="sectionModalVisible"
      :title="isEditSection ? '编辑小节' : '添加小节'"
      width="700px"
      @ok="handleSectionSubmit"
    >
      <a-form
        :model="sectionForm"
        :label-col="{ span: 5 }"
      >
        <a-form-item
          label="小节名称"
          required
        >
          <a-input
            v-model:value="sectionForm.sectionName"
            placeholder="请输入小节名称"
          />
        </a-form-item>

        <a-form-item
          label="类型"
          required
        >
          <a-radio-group
            v-model:value="sectionForm.sectionType"
            @change="onSectionTypeChange"
          >
            <a-radio value="video">
              📹 视频
            </a-radio>
            <a-radio value="pdf">
              📄 PDF
            </a-radio>
            <a-radio value="exam">
              📝 考试
            </a-radio>
          </a-radio-group>
        </a-form-item>

        <!-- 文件上传（视频/PDF） -->
        <a-form-item
          v-if="sectionForm.sectionType !== 'exam'"
          :label="sectionForm.sectionType === 'video' ? '视频文件' : 'PDF 文件'"
          required
        >
          <FileUpload
            v-model="sectionForm.contentUrl"
            :type="sectionForm.sectionType"
            :max-size="100"
            @change="onFileUploadChange"
          />
          <div
            v-if="sectionForm.contentUrl"
            style="margin-top: 8px; font-size: 12px; color: #666"
          >
            文件地址：{{ sectionForm.contentUrl }}
          </div>
        </a-form-item>

        <!-- 考试选择 -->
        <a-form-item
          v-if="sectionForm.sectionType === 'exam'"
          label="选择试卷"
          required
        >
          <a-select
            v-model:value="sectionForm.examId"
            placeholder="请选择试卷"
          >
            <a-select-option
              v-for="paper in availablePapers"
              :key="paper.id"
              :value="paper.id"
            >
              {{ paper.paperName }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          v-if="sectionForm.sectionType !== 'exam'"
          :label="sectionForm.sectionType === 'video' ? '时长 (秒)' : '页数'"
        >
          <a-input-number
            v-model:value="sectionForm.duration"
            :min="1"
          />
        </a-form-item>

        <a-form-item
          v-if="sectionForm.sectionType === 'pdf'"
          label="PDF 阅读时长 (秒)"
        >
          <a-input-number
            v-model:value="sectionForm.pdfReadTime"
            :min="60"
            :step="60"
          />
          <div style="font-size: 12px; color: #999">
            学生需要阅读的最短时长（秒）
          </div>
        </a-form-item>

        <a-form-item
          v-if="sectionForm.sectionType === 'video'"
          label="允许快进"
        >
          <a-switch
            v-model:checked="sectionForm.isAllowSeek"
            checked-children="是"
            unchecked-children="否"
          />
          <div style="font-size: 12px; color: #999; margin-top: 4px">
            禁止快进时学生必须完整观看视频
          </div>
        </a-form-item>

        <a-form-item
          v-if="sectionForm.sectionType === 'video'"
          label="禁止拖动"
        >
          <a-switch
            v-model:checked="sectionForm.isNoDrag"
            checked-children="是"
            unchecked-children="否"
          />
          <div style="font-size: 12px; color: #999; margin-top: 4px">
            启用后学生无法拖动视频进度条
          </div>
        </a-form-item>

        <a-form-item label="按步骤学习">
          <a-switch
            v-model:checked="sectionForm.isStepLearning"
            checked-children="是"
            unchecked-children="否"
          />
          <div style="font-size: 12px; color: #999; margin-top: 4px">
            启用后必须按顺序完成每个小节
          </div>
        </a-form-item>

        <a-form-item label="免费试看">
          <a-switch
            v-model:checked="sectionForm.isFree"
            checked-children="是"
            unchecked-children="否"
          />
        </a-form-item>

        <a-form-item label="排序">
          <a-input-number
            v-model:value="sectionForm.sort"
            :min="1"
          />
        </a-form-item>

        <a-form-item label="状态">
          <a-switch
            v-model:checked="sectionForm.status"
            checked-children="启用"
            unchecked-children="禁用"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <PreviewModal
      v-model:open="previewVisible"
      :url="previewUrl"
      :type="previewType"
      :title="previewTitle"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { courseApi } from '@/api/course'
import { examApi } from '@/api/exam'
import FileUpload from '@/components/Upload/FileUpload.vue'
import PreviewModal from '@/components/PreviewModal.vue'

const router = useRouter()
const route = useRoute()
const courseId = route.params.id

const sectionColumns = [
  { title: '小节名称', dataIndex: 'sectionName', key: 'sectionName' },
  { title: '类型', key: 'sectionType', width: 100 },
  { title: '时长/页数', dataIndex: 'duration', width: 100 },
  { title: '快进', key: 'isAllowSeek', width: 80 },
  { title: '禁止拖动', key: 'isNoDrag', width: 100 },
  { title: '操作', key: 'action', width: 200 }
]

const chapters = ref([])
const sectionsMap = ref({})
const activeChapters = ref([])
const availablePapers = ref([])

const chapterModalVisible = ref(false)
const isEditChapter = ref(false)
const chapterForm = reactive({ id: null, courseId, chapterName: '', chapterOrder: 1 })

const sectionModalVisible = ref(false)
const isEditSection = ref(false)
const sectionForm = reactive({
  id: null,
  chapterId: null,
  sectionName: '',
  sectionType: 'video',
  contentUrl: '',
  duration: 0,
  pdfReadTime: 300,
  isAllowSeek: true,
  isNoDrag: false,
  isStepLearning: false,
  isFree: false,
  sort: 1,
  status: true,
  examId: null
})

const previewVisible = ref(false)
const previewUrl = ref('')
const previewType = ref('video')
const previewTitle = ref('文件预览')

const onSectionTypeChange = (e) => {
  sectionForm.contentUrl = ''
  sectionForm.examId = null
}

const onFileUploadChange = (data) => {
  console.log('文件上传成功', data)
}

const handlePreview = (section) => {
  if (!section.contentUrl) {
    message.warning('暂无文件可预览')
    return
  }
  previewUrl.value = section.contentUrl
  previewType.value = section.sectionType
  previewTitle.value = `${section.sectionName} - ${getSectionTypeText(section.sectionType)}预览`
  previewVisible.value = true
}

const getSectionTypeText = (type) => {
  const typeMap = {
    video: '视频',
    pdf: 'PDF',
    exam: '考试'
  }
  return typeMap[type] || type
}

const getSectionTypeColor = (type) => {
  const colorMap = {
    video: 'blue',
    pdf: 'green',
    exam: 'orange'
  }
  return colorMap[type] || 'default'
}

const loadChapters = async () => {
  if (!courseId) {
    message.error('课程 ID 不存在')
    return
  }
  try {
    const res = await courseApi.getChapters(courseId)
    chapters.value = res.data
    activeChapters.value = res.data.map(c => c.id)
    for (const chapter of res.data) {
      const secRes = await courseApi.getSections(chapter.id)
      sectionsMap.value[chapter.id] = secRes.data
    }
  } catch (e) {
    console.error('加载章节失败', e)
  }
}

const loadPapers = async () => {
  const res = await examApi.paperList({ pageNum: 1, pageSize: 100, status: 1 })
  availablePapers.value = res.data.records
}

const getSections = (chapterId) => sectionsMap.value[chapterId] || []

const handleAddChapter = () => {
  isEditChapter.value = false
  Object.assign(chapterForm, { id: null, courseId, chapterName: '', chapterOrder: 1 })
  chapterModalVisible.value = true
}

const handleEditChapter = (chapter) => {
  isEditChapter.value = true
  Object.assign(chapterForm, chapter)
  chapterModalVisible.value = true
}

const handleChapterSubmit = async () => {
  if (!chapterForm.chapterName) {
    message.error('请输入章节名称')
    return
  }
  try {
    if (isEditChapter.value) {
      await courseApi.updateChapter(chapterForm)
      message.success('更新成功')
    } else {
      await courseApi.addChapter(chapterForm)
      message.success('添加成功')
    }
    chapterModalVisible.value = false
    loadChapters()
  } catch (e) {
    console.error(e)
  }
}

const handleDeleteChapter = async (id) => {
  await courseApi.deleteChapter(id)
  message.success('删除成功')
  loadChapters()
}

const handleAddSection = (chapterId) => {
  isEditSection.value = false
  Object.assign(sectionForm, {
    id: null,
    chapterId,
    sectionName: '',
    sectionType: 'video',
    contentUrl: '',
    duration: 0,
    pdfReadTime: 300,
    isAllowSeek: true,
    isNoDrag: false,
    isStepLearning: false,
    isFree: false,
    sort: 1,
    status: true,
    examId: null
  })
  sectionModalVisible.value = true
}

const handleEditSection = (section) => {
  isEditSection.value = true
  Object.assign(sectionForm, {
    ...section,
    isAllowSeek: section.isAllowSeek === 1,
    isNoDrag: section.isNoDrag === 1,
    isStepLearning: section.isStepLearning === 1,
    isFree: section.isFree === 1,
    status: section.status === 1,
    examId: section.examId || null
  })
  sectionModalVisible.value = true
}

const handleSectionSubmit = async () => {
  if (!sectionForm.sectionName) {
    message.error('请输入小节名称')
    return
  }

  if (sectionForm.sectionType === 'exam') {
    if (!sectionForm.examId) {
      message.error('请选择试卷')
      return
    }
  } else {
    if (!sectionForm.contentUrl) {
      message.error('请上传文件')
      return
    }
  }

  try {
    const data = {
      ...sectionForm,
      isAllowSeek: sectionForm.isAllowSeek ? 1 : 0,
      isNoDrag: sectionForm.isNoDrag ? 1 : 0,
      isStepLearning: sectionForm.isStepLearning ? 1 : 0,
      isFree: sectionForm.isFree ? 1 : 0,
      status: sectionForm.status ? 1 : 0
    }
    if (isEditSection.value) {
      await courseApi.updateSection(data)
      message.success('更新成功')
    } else {
      await courseApi.addSection(data)
      message.success('添加成功')
    }
    sectionModalVisible.value = false
    loadChapters()
  } catch (e) {
    console.error(e)
  }
}

const handleDeleteSection = async (id) => {
  await courseApi.deleteSection(id)
  message.success('删除成功')
  loadChapters()
}

onMounted(() => {
  loadChapters()
  loadPapers()
})
</script>

<style scoped>
.page-container {
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
}
</style>
