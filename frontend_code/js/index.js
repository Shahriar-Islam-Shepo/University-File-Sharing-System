 




const fileGrid = document.getElementById('fileGrid');
const searchInput = document.getElementById('searchInput');
const categoryFilter = document.getElementById('categoryFilter');

async function fetchFileData(start, end) {
    try {
        const response = await fetch(`http://localhost:8080/api/files?start=${start}&end=${end}`);
        
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const data = await response.json();
        renderCards(data); 
    } catch (error) {
        console.error('Error fetching data:', error);
        // We only show the error if the grid is empty; otherwise, we don't want to hide existing cards
        if(fileGrid.children.length === 0) {
            fileGrid.innerHTML = `<p style="color:red;">Failed to load files from server.</p>`;
        }
    }
}

function renderCards(data) {
    // REMOVED: fileGrid.innerHTML = ''; 
    // This allows previous cards to stay on the screen.

    // Check if data is empty AND the grid is currently empty
    if (data.length === 0 && fileGrid.children.length === 0) {
        fileGrid.innerHTML = '<p>No files found.</p>';
        return;
    }

    data.forEach(item => {
        const card = `
            <div class="card">
                <div class="card-header">
                    <i class="fas ${getIcon(item.cat)} file-icon"></i>
                    <span class="category-tag">${item.cat}</span>
                </div>
                <div class="card-body">
                    <h4>${item.title}</h4>
                    <p>Topic: <b>${item.topic}</b><br>By: ${item.user}</p>
                </div>
                <div class="card-footer">
                    <span>${item.date}</span>
                    <button class="view-btn" onclick="openFile(${item.id})">view</button>
                    <button class="download-btn" onclick="downloadFile(${item.id})"><i class="fas fa-download"></i></button>
                </div>
            </div>
        `;
        // This method adds new cards to the bottom without refreshing the whole list
        fileGrid.insertAdjacentHTML('beforeend', card);
    });
}

let currentStart = 1;
const pageSize = 2;

const loadMoreBtn = document.querySelector('.load_more');
loadMoreBtn.addEventListener('click', () => {
    currentStart += pageSize; 
    let currentEnd = currentStart + pageSize - 1;
    fetchFileData(currentStart, currentEnd);
});

// Initial Load
fetchFileData(1, 2);

function getIcon(cat) {
    switch(cat) {
        case 'book': return 'fa-book';
        case 'notes': return 'fa-file-lines';
        case 'assignment': return 'fa-clipboard-check';
        case 'journal': return 'fa-newspaper';
        default: return 'fa-file';
    }
}

// Search & Filter Logic
function filterContent() {
    const searchTerm = searchInput.value.toLowerCase();
    const categoryTerm = categoryFilter.value;

    // IMPORTANT: When searching/filtering, we DO want to clear the grid 
    // to show only the relevant results.
    fileGrid.innerHTML = '';

    const filtered = mockData.filter(item => {
        const matchesSearch = item.title.toLowerCase().includes(searchTerm) || item.topic.toLowerCase().includes(searchTerm);
        const matchesCat = categoryTerm === 'all' || item.cat === categoryTerm;
        return matchesSearch && matchesCat;
    });
    renderCards(filtered);
}

searchInput.addEventListener('input', filterContent);
categoryFilter.addEventListener('change', filterContent);



// view file 

function openFile(fileId) {
    // The URL matches our Backend @GetMapping
    const url = `http://localhost:8080/api/files/view/${fileId}`;
    
    // Open in a new tab
    window.open(url, '_blank');
}

//download file

function downloadFile(fileId) {
    // The URL matches our Backend @GetMapping
    const url = `http://localhost:8080/api/files/download/${fileId}`;
    
    // Open in a new tab
    window.open(url, '_blank');
}