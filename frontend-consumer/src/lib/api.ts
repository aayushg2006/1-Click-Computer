const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1/public';

// Helper for handling JSON responses
async function fetchJson<T>(endpoint: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options?.headers,
    },
  });

  if (!response.ok) {
    const errorBody = await response.text();
    throw new Error(`API Error: ${response.status} ${response.statusText} - ${errorBody}`);
  }

  // Handle 204 No Content
  if (response.status === 204) {
    return {} as T;
  }

  return response.json();
}

export const api = {
  catalog: {
    getProducts: () => fetchJson<any[]>('/catalog/products', { cache: 'no-store' }),
  },
  reservations: {
    create: (data: any) => fetchJson<any>('/reservations/create', {
      method: 'POST',
      body: JSON.stringify(data)
    })
  },
  pcBuilder: {
    getQuote: (data: any) => fetchJson<any>('/pc-builder/quote', {
      method: 'POST',
      body: JSON.stringify(data)
    })
  },
  repairs: {
    bookDoorstep: (data: any) => fetchJson<any>('/repairs/doorstep', {
      method: 'POST',
      body: JSON.stringify(data)
    })
  },
  tickets: {
    getStatus: (trackingCode: string) => fetchJson<any>(`/tickets/${trackingCode}`, { cache: 'no-store' }),
    getUpdates: (trackingCode: string) => fetchJson<any>(`/tickets/${trackingCode}/updates`, { cache: 'no-store' })
  },
  cctvEstimator: {
    estimate: (data: any) => fetchJson<any>('/cctv-estimator/estimate', {
      method: 'POST',
      body: JSON.stringify(data)
    })
  },
  customerAccess: {
    verify: (data: any) => fetchJson<any>('/customer-access/verify', {
      method: 'POST',
      body: JSON.stringify(data)
    }),
    getTicketDetails: (trackingCode: string, token: string, last4: string) => fetchJson<any>(`/customer-access/tickets/${trackingCode}`, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'X-Last-4': last4
      }
    })
  }
};
