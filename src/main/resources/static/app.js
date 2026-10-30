const classSelect = document.getElementById('classid');
const reloadBtn = document.getElementById('reload');
const statusText = document.getElementById('status');
const tbody = document.getElementById('studentRows');
const emptyTip = document.getElementById('empty');
const addForm = document.getElementById('addForm');
const formTitle = document.getElementById('formTitle');
const submitBtn = document.getElementById('submitBtn');

let currentList = [];
let editingId = null;

async function loadClasses() {
  const res = await fetch('/api/classes');
  if (!res.ok) {
    statusText.textContent = '班级加载失败：HTTP ' + res.status;
    return;
  }
  const list = await res.json();
  for (const item of list) {
    const option = document.createElement('option');
    option.value = item.classid;
    option.textContent = item.classname;
    classSelect.appendChild(option);
  }
}

async function loadStudents() {
  const classid = classSelect.value;
  if (!classid) {
    tbody.innerHTML = '';
    currentList = [];
    emptyTip.hidden = false;
    emptyTip.textContent = '先在上面选一个班级';
    statusText.textContent = '';
    return;
  }
  statusText.textContent = '加载中…';
  const res = await fetch('/api/students?classid=' + encodeURIComponent(classid));
  if (!res.ok) {
    statusText.textContent = '加载失败：HTTP ' + res.status;
    return;
  }
  const list = await res.json();
  renderRows(list);
  statusText.textContent = '共 ' + list.length + ' 条';
}

function renderRows(list) {
  currentList = list;
  tbody.innerHTML = '';
  if (list.length === 0) {
    emptyTip.hidden = false;
    emptyTip.textContent = '这个班还没有学生';
    return;
  }
  emptyTip.hidden = true;
  for (const s of list) {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${s.id}</td>
      <td>${s.name}</td>
      <td>${s.gender}</td>
      <td>${s.age}</td>
      <td>${s.classid}</td>
      <td>${s.phone ?? ''}</td>
      <td>
        <button type="button" class="update" data-id="${s.id}">编辑</button>
        <button type="button" class="delete" data-id="${s.id}">删除</button>
      </td>
    `;
    tbody.appendChild(tr);
  }
}

function readForm() {
  const fd = new FormData(addForm);
  return {
    name: fd.get('name'),
    gender: fd.get('gender'),
    age: Number(fd.get('age')),
    classid: Number(classSelect.value),
    phone: fd.get('phone')
  };
}

function finishEdit() {
  editingId = null;
  addForm.reset();
  formTitle.textContent = '新增学生';
  submitBtn.textContent = '提交';
}

function startEdit(id) {
  const s = currentList.find(item => String(item.id) === String(id));
  if (!s) return;
  editingId = s.id;
  addForm.elements['name'].value = s.name ?? '';
  addForm.elements['gender'].value = s.gender ?? '男';
  addForm.elements['age'].value = s.age ?? '';
  addForm.elements['phone'].value = s.phone ?? '';
  formTitle.textContent = '编辑学生（ID ' + s.id + '）';
  submitBtn.textContent = '保存修改';
  statusText.textContent = '改完点「保存修改」';
  addForm.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

async function submitForm(e) {
  e.preventDefault();
  if (!classSelect.value) {
    statusText.textContent = '请先选择班级';
    return;
  }
  const payload = readForm();
  if (!payload.name || !payload.gender || !Number.isFinite(payload.age)) {
    statusText.textContent = '表单填写有误';
    return;
  }
  if (editingId === null) {
    await createStudent(payload);
  } else {
    await updateStudent(editingId, payload);
  }
}

async function createStudent(payload) {
  try {
    const res = await fetch('/api/students', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (res.status === 409) {
      statusText.textContent = '名字重复，添加失败';
      return;
    }
    if (res.status !== 201) {
      statusText.textContent = '失败：HTTP ' + res.status;
      return;
    }
    finishEdit();
    await loadStudents();
    statusText.textContent = '新增成功';
  } catch (err) {
    statusText.textContent = '网络请求失败';
  }
}

async function updateStudent(id, payload) {
  try {
    const res = await fetch('/api/students/' + id, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
    if (res.status === 404) {
      statusText.textContent = '这条记录已经不在了';
      return;
    }
    if (res.status !== 200) {
      statusText.textContent = '失败：HTTP ' + res.status;
      return;
    }
    finishEdit();
    await loadStudents();
    statusText.textContent = '修改成功';
  } catch (err) {
    statusText.textContent = '网络请求失败';
  }
}

async function removeStudent(id) {
  try {
    const res = await fetch('/api/students/' + id, { method: 'DELETE' });
    if (res.status === 404) {
      statusText.textContent = '该学生不存在';
      return;
    }
    if (res.status !== 204) {
      statusText.textContent = '失败：HTTP ' + res.status;
      return;
    }
    if (String(editingId) === String(id)) {
      finishEdit();
    }
    await loadStudents();
    statusText.textContent = '删除成功';
  } catch (err) {
    statusText.textContent = '网络请求失败';
  }
}

classSelect.addEventListener('change', loadStudents);
reloadBtn.addEventListener('click', () => {
  finishEdit();
  loadStudents();
});
addForm.addEventListener('submit', submitForm);

tbody.addEventListener('click', async (e) => {
  const target = e.target;
  if (target.classList.contains('update')) {
    startEdit(target.dataset.id);
    return;
  }
  if (!target.classList.contains('delete')) return;
  if (!confirm('确定删除？')) return;
  await removeStudent(target.dataset.id);
});

loadClasses();
