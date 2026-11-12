// FoodHub Application State
let allRestaurants = [];
let activeCuisine = 'All';
let cart = []; // Array of { id, name, price, quantity, restaurantId, restaurantName }
let activeRestaurant = null;
let currentSearch = '';

// Currency: 1 USD = 82 INR
const USD_TO_INR = 82;
function toRupees(usd) { return usd * USD_TO_INR; }
function formatRupees(usd) { return '₹' + toRupees(usd).toFixed(0); }

// DOM Elements
const restaurantsGrid = document.getElementById('restaurantsGrid');
const resultsCount = document.getElementById('resultsCount');
const searchInput = document.getElementById('searchInput');
const clearSearchBtn = document.getElementById('clearSearchBtn');
const cartCount = document.getElementById('cartCount');
const cartHeaderCount = document.getElementById('cartHeaderCount');
const cartItemsList = document.getElementById('cartItemsList');
const cartSubtotal = document.getElementById('cartSubtotal');
const cartTax = document.getElementById('cartTax');
const cartTotal = document.getElementById('cartTotal');
const proceedCheckoutBtn = document.getElementById('proceedCheckoutBtn');

// Initialize
document.addEventListener('DOMContentLoaded', () => {
  fetchRestaurants();
  checkBackendHealth();
  setupEventListeners();
  loadCartFromStorage();
});

function setupEventListeners() {
  searchInput.addEventListener('input', (e) => {
    currentSearch = e.target.value.trim();
    clearSearchBtn.style.display = currentSearch.length > 0 ? 'block' : 'none';
    renderRestaurants();
  });
}

function clearSearch() {
  searchInput.value = '';
  currentSearch = '';
  clearSearchBtn.style.display = 'none';
  renderRestaurants();
}

function resetToHome() {
  filterByCuisine('All');
  clearSearch();
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

// Fetch all restaurants from Spring Boot REST API
async function fetchRestaurants() {
  try {
    resultsCount.textContent = 'Loading eateries...';
    const res = await fetch('/api/restaurants');
    if (!res.ok) throw new Error('Failed to load restaurants');
    allRestaurants = await res.json();
    renderRestaurants();
  } catch (err) {
    console.error('Error fetching restaurants:', err);
    restaurantsGrid.innerHTML = `
      <div style="grid-column: 1 / -1; text-align: center; padding: 3rem; background: #fff; border-radius: 14px;">
        <h3>⚠️ Could not connect to API</h3>
        <p style="color: #747d8c; margin-top: 0.5rem;">Ensure Spring Boot is running at http://localhost:8088</p>
        <button class="add-cart-btn" style="margin-top: 1rem;" onclick="fetchRestaurants()">Retry Connection</button>
      </div>
    `;
    resultsCount.textContent = 'Connection Error';
  }
}

// Check Backend Health
async function checkBackendHealth() {
  const badge = document.getElementById('systemHealthBadge');
  try {
    const res = await fetch('/api/health');
    if (res.ok) {
      const data = await res.json();
      badge.innerHTML = `
        <span class="status-dot online"></span>
        <span class="badge-text">Spring Boot Live (${data.restaurantsCount} spots)</span>
      `;
      badge.classList.remove('offline');
      badge.classList.add('online');
    } else {
      throw new Error();
    }
  } catch (err) {
    badge.innerHTML = `
      <span class="status-dot" style="background:#e74c3c;"></span>
      <span class="badge-text">Backend Offline</span>
    `;
  }
}

// Filter by cuisine
function filterByCuisine(cuisine) {
  activeCuisine = cuisine;
  const pillBtns = document.querySelectorAll('.cuisine-pills .pill-btn');
  pillBtns.forEach(btn => {
    if (btn.getAttribute('data-cuisine') === cuisine) {
      btn.classList.add('active');
    } else {
      btn.classList.remove('active');
    }
  });

  const heading = document.getElementById('restaurantsHeading');
  if (cuisine === 'All') {
    heading.textContent = 'Popular Restaurants';
  } else {
    heading.textContent = `${cuisine} Kitchens`;
  }

  renderRestaurants();
}

// Render restaurants matching filters
function renderRestaurants() {
  let filtered = allRestaurants;

  if (activeCuisine !== 'All') {
    filtered = filtered.filter(r => r.cuisine && r.cuisine.toLowerCase() === activeCuisine.toLowerCase());
  }

  if (currentSearch.length > 0) {
    const q = currentSearch.toLowerCase();
    filtered = filtered.filter(r =>
      r.name.toLowerCase().includes(q) ||
      (r.cuisine && r.cuisine.toLowerCase().includes(q)) ||
      (r.description && r.description.toLowerCase().includes(q))
    );
  }

  resultsCount.textContent = `Showing ${filtered.length} restaurant${filtered.length === 1 ? '' : 's'}`;

  if (filtered.length === 0) {
    restaurantsGrid.innerHTML = `
      <div style="grid-column: 1 / -1; text-align: center; padding: 4rem 1rem; background: #fff; border-radius: 14px;">
        <span style="font-size: 3rem;">🔍</span>
        <h3 style="margin-top: 1rem;">No restaurants found</h3>
        <p style="color: #747d8c; margin-top: 0.5rem;">Try adjusting your search terms or selecting another category.</p>
        <button class="pill-btn" style="background: var(--primary); margin-top: 1rem;" onclick="resetToHome()">View All Restaurants</button>
      </div>
    `;
    return;
  }

  restaurantsGrid.innerHTML = filtered.map(r => `
    <div class="restaurant-card" onclick="openRestaurantMenu(${r.id})">
      <div class="card-image-wrap">
        <img src="${r.imageUrl || 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800&q=80'}" alt="${escapeHtml(r.name)}" loading="lazy" />
        <span class="card-badge-rating">⭐ ${r.rating ? r.rating.toFixed(1) : '4.8'}</span>
        <span class="card-badge-time">🕒 ${r.deliveryTime || '20-30 min'}</span>
      </div>
      <div class="card-body">
        <div class="card-title-row">
          <h3 class="restaurant-name">${escapeHtml(r.name)}</h3>
          <span class="cuisine-tag">${escapeHtml(r.cuisine || 'Gourmet')}</span>
        </div>
        <p class="restaurant-desc">${escapeHtml(r.description || 'Delicious freshly prepared meals delivered right to your doorstep.')}</p>
        <div class="card-footer">
          <span class="delivery-fee">🚲 ${formatRupees(r.deliveryFee || 2.49)} delivery</span>
          <button class="view-menu-btn" onclick="event.stopPropagation(); openRestaurantMenu(${r.id})">View Menu →</button>
        </div>
      </div>
    </div>
  `).join('');
}

// Open Restaurant Menu Modal
async function openRestaurantMenu(restaurantId) {
  const restaurant = allRestaurants.find(r => r.id === restaurantId);
  if (!restaurant) return;
  activeRestaurant = restaurant;

  const heroInfo = document.getElementById('modalHeroInfo');
  const modalHero = document.getElementById('modalHero');
  modalHero.style.backgroundImage = `url('${restaurant.imageUrl}')`;

  heroInfo.innerHTML = `
    <h2>${escapeHtml(restaurant.name)}</h2>
    <div class="modal-hero-meta">
      <span>⭐ ${restaurant.rating ? restaurant.rating.toFixed(1) : '4.8'}</span>
      <span>•</span>
      <span>🍴 ${escapeHtml(restaurant.cuisine)}</span>
      <span>•</span>
      <span>🕒 ${restaurant.deliveryTime || '20-30 min'}</span>
      <span>•</span>
      <span>📍 ${escapeHtml(restaurant.address || 'Local Downtown')}</span>
    </div>
  `;

  const menuGrid = document.getElementById('menuItemsGrid');
  menuGrid.innerHTML = '<div style="grid-column: 1 / -1; text-align: center; padding: 2rem;">Loading menu items...</div>';
  openModal('menuModal');

  try {
    const res = await fetch(`/api/restaurants/${restaurantId}/menu`);
    if (!res.ok) throw new Error('Failed to load menu');
    const items = await res.json();

    if (items.length === 0) {
      menuGrid.innerHTML = '<div style="grid-column: 1 / -1; text-align: center; padding: 2rem; color: #747d8c;">No menu items listed yet.</div>';
      return;
    }

    menuGrid.innerHTML = items.map(item => `
      <div class="menu-item-card">
        <div class="menu-item-top">
          <div class="menu-item-info">
            <h4 class="menu-item-name">
              ${item.isVegetarian ? '<span class="veg-icon" title="Vegetarian"></span>' : ''}
              ${escapeHtml(item.name)}
            </h4>
            <p class="menu-item-desc">${escapeHtml(item.description || '')}</p>
          </div>
          ${item.imageUrl ? `<img src="${item.imageUrl}" class="menu-item-img" alt="${escapeHtml(item.name)}" loading="lazy" />` : ''}
        </div>
        <div class="menu-item-bottom">
          <span class="menu-item-price">${formatRupees(item.price)}</span>
          <button class="add-cart-btn" onclick="addToCart(${item.id}, '${escapeHtml(item.name)}', ${item.price}, ${restaurant.id}, '${escapeHtml(restaurant.name)}')">
            + Add to Cart
          </button>
        </div>
      </div>
    `).join('');
  } catch (err) {
    console.error('Failed to load menu items:', err);
    menuGrid.innerHTML = '<div style="grid-column: 1 / -1; text-align: center; padding: 2rem; color: #e74c3c;">Failed to load menu items.</div>';
  }
}

// Cart Management
function addToCart(itemId, itemName, itemPrice, restId, restName) {
  const existing = cart.find(i => i.id === itemId);
  if (existing) {
    existing.quantity += 1;
  } else {
    cart.push({
      id: itemId,
      name: itemName,
      price: itemPrice,
      quantity: 1,
      restaurantId: restId,
      restaurantName: restName
    });
  }

  saveCart();
  updateCartUI();
  showToast(`Added "${itemName}" to cart!`);
}

function updateQuantity(itemId, delta) {
  const item = cart.find(i => i.id === itemId);
  if (!item) return;

  item.quantity += delta;
  if (item.quantity <= 0) {
    cart = cart.filter(i => i.id !== itemId);
  }

  saveCart();
  updateCartUI();
}

function saveCart() {
  localStorage.setItem('foodhub_cart', JSON.stringify(cart));
}

function loadCartFromStorage() {
  try {
    const saved = localStorage.getItem('foodhub_cart');
    if (saved) {
      cart = JSON.parse(saved);
      updateCartUI();
    }
  } catch (e) {
    cart = [];
  }
}

function updateCartUI() {
  const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);
  cartCount.textContent = totalItems;
  cartHeaderCount.textContent = `${totalItems} item${totalItems === 1 ? '' : 's'}`;

  // All prices stored in USD internally, displayed as INR
  const subtotalUSD = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  const deliveryFeeUSD = totalItems > 0 ? 2.99 : 0.00;
  const taxUSD = subtotalUSD * 0.05;
  const totalUSD = subtotalUSD + deliveryFeeUSD + taxUSD;

  cartSubtotal.textContent = formatRupees(subtotalUSD);
  cartTax.textContent = formatRupees(taxUSD);
  cartTotal.textContent = formatRupees(totalUSD);
  document.getElementById('cartDeliveryFee').textContent = formatRupees(deliveryFeeUSD);

  if (cart.length === 0) {
    cartItemsList.innerHTML = `
      <div class="empty-cart-state">
        <span class="empty-icon">🛒</span>
        <h4>Your cart is empty</h4>
        <p>Explore our restaurants and add some tasty dishes to get started!</p>
      </div>
    `;
    proceedCheckoutBtn.disabled = true;
  } else {
    cartItemsList.innerHTML = cart.map(item => `
      <div class="cart-item-row">
        <div class="cart-item-left">
          <div class="cart-item-name">${escapeHtml(item.name)}</div>
          <div class="cart-item-price-each">${formatRupees(item.price)} each</div>
        </div>
        <div class="cart-item-controls">
          <button class="qty-btn" onclick="updateQuantity(${item.id}, -1)">−</button>
          <span class="qty-val">${item.quantity}</span>
          <button class="qty-btn" onclick="updateQuantity(${item.id}, 1)">+</button>
        </div>
      </div>
    `).join('');
    proceedCheckoutBtn.disabled = false;
  }
}

function toggleCartDrawer() {
  const drawer = document.getElementById('cartDrawer');
  const backdrop = document.getElementById('cartBackdrop');
  drawer.classList.toggle('open');
  backdrop.classList.toggle('open');
}

function closeCartDrawer() {
  document.getElementById('cartDrawer').classList.remove('open');
  document.getElementById('cartBackdrop').classList.remove('open');
}

// Checkout Modal
function openCheckoutModal() {
  closeCartDrawer();
  const summaryBox = document.getElementById('checkoutSummaryItems');
  const subtotalUSD = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  const totalUSD = subtotalUSD + 2.99 + (subtotalUSD * 0.05);

  summaryBox.innerHTML = cart.map(item => `
    <div style="display:flex; justify-content:space-between; font-size:0.85rem; margin-bottom:0.3rem;">
      <span>${item.quantity}x ${escapeHtml(item.name)}</span>
      <span>${formatRupees(item.price * item.quantity)}</span>
    </div>
  `).join('');

  document.getElementById('checkoutFinalTotal').textContent = formatRupees(totalUSD);
  openModal('checkoutModal');
}

async function handlePlaceOrder(event) {
  event.preventDefault();
  const submitBtn = document.getElementById('placeOrderSubmitBtn');
  submitBtn.disabled = true;
  submitBtn.textContent = 'Processing Order...';

  const customerName = document.getElementById('custName').value.trim();
  const customerEmail = document.getElementById('custEmail').value.trim();
  const customerPhone = document.getElementById('custPhone').value.trim();
  const deliveryAddress = document.getElementById('custAddress').value.trim();
  const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked').value;

  const orderPayload = {
    customerName,
    customerEmail,
    customerPhone,
    deliveryAddress,
    paymentMethod,
    items: cart.map(item => ({
      menuItemId: item.id,
      itemName: item.name,
      price: item.price,
      quantity: item.quantity
    }))
  };

  try {
    const res = await fetch('/api/orders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(orderPayload)
    });

    if (!res.ok) {
      const errData = await res.json();
      throw new Error(errData.error || 'Failed to place order');
    }

    const savedOrder = await res.json();

    // Clear Cart
    cart = [];
    saveCart();
    updateCartUI();

    // Show Success Modal
    closeModal('checkoutModal');
    document.getElementById('confirmedOrderId').textContent = savedOrder.id;
    document.getElementById('confirmedOrderDetails').innerHTML = `
      <div style="background:#f8f9fa; padding:1rem; border-radius:8px; text-align:left; font-size:0.88rem;">
        <div style="margin-bottom:0.4rem;"><strong>Deliver to:</strong> ${escapeHtml(savedOrder.customerName)} (${escapeHtml(savedOrder.deliveryAddress)})</div>
        <div style="margin-bottom:0.4rem;"><strong>Payment Method:</strong> ${escapeHtml(savedOrder.paymentMethod)}</div>
        <div style="margin-bottom:0.4rem;"><strong>Total Paid:</strong> ${formatRupees(savedOrder.totalAmount)}</div>
        <div><strong>Items:</strong> ${savedOrder.items.map(i => `${i.quantity}x ${escapeHtml(i.itemName)}`).join(', ')}</div>
      </div>
    `;
    openModal('orderSuccessModal');
    checkBackendHealth();
  } catch (err) {
    alert('Error placing order: ' + err.message);
  } finally {
    submitBtn.disabled = false;
    submitBtn.textContent = 'Place Order Now';
  }
}

// Orders History Modal
async function openOrdersModal() {
  openModal('ordersModal');
  const container = document.getElementById('ordersHistoryList');
  container.innerHTML = '<p style="text-align:center; padding:2rem; color:#747d8c;">Loading past orders...</p>';

  try {
    const res = await fetch('/api/orders');
    if (!res.ok) throw new Error();
    const orders = await res.json();

    if (orders.length === 0) {
      container.innerHTML = `
        <div style="text-align:center; padding:3rem 1rem; color:#747d8c;">
          <span style="font-size:2.5rem; display:block; margin-bottom:0.5rem;">📦</span>
          <h4>No orders yet</h4>
          <p>Orders you place will appear here with live status tracking.</p>
        </div>
      `;
      return;
    }

    container.innerHTML = orders.map(ord => {
      const formattedDate = new Date(ord.createdAt).toLocaleString();
      return `
        <div class="order-history-card">
          <div class="order-history-header">
            <span class="order-history-id">Order #${ord.id} • ${escapeHtml(ord.customerName)}</span>
            <span class="order-status-badge status-${ord.status}">${ord.status}</span>
          </div>
          <div class="order-history-items">
            ${ord.items.map(i => `${i.quantity}x ${escapeHtml(i.itemName)} (${formatRupees(i.price * i.quantity)})`).join(', ')}
          </div>
          <div class="order-history-footer">
            <span style="color:#747d8c; font-size:0.8rem;">🕒 ${formattedDate}</span>
            <span>Total: ${formatRupees(ord.totalAmount)}</span>
          </div>
        </div>
      `;
    }).join('');
  } catch (err) {
    container.innerHTML = '<p style="text-align:center; color:#e74c3c;">Failed to load order history.</p>';
  }
}

// Health Modal
async function showHealthModal() {
  openModal('healthModal');
  const box = document.getElementById('healthInfoContent');
  box.textContent = 'Probing Spring Boot API...';

  try {
    const res = await fetch('/api/health');
    const data = await res.json();
    box.textContent = JSON.stringify(data, null, 2);
  } catch (e) {
    box.textContent = 'API Connection Error: Backend is not reachable.';
  }
}

// Generic Modal Controls
function openModal(id) {
  document.getElementById(id).classList.add('open');
  document.body.style.overflow = 'hidden';
}

function closeModal(id) {
  document.getElementById(id).classList.remove('open');
  document.body.style.overflow = 'auto';
}

function closeOnBackdrop(event, id) {
  if (event.target.id === id) {
    closeModal(id);
  }
}

// Toast
function showToast(message) {
  const container = document.getElementById('toastContainer');
  const toast = document.createElement('div');
  toast.className = 'toast';
  toast.innerHTML = `<span>✓</span><span>${escapeHtml(message)}</span>`;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    setTimeout(() => toast.remove(), 300);
  }, 2500);
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}
