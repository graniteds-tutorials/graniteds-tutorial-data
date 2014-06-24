package org.graniteds.tutorial.data.client.util;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.RejectedExecutionException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;


public class AsyncImageLoader {
	
	private static enum State {
		QUEUED,
		LOADING,
		LOADED,
		DISCONNECTED,
		ERROR
	}
	
	private final int viewTag = AsyncImageLoader.class.hashCode();
	
	private static final class ImageLoadState {
		private String url = null;
		private LoadImageTask loadTask = null;
		private State state = null;
		
		public void update(ImageView view, String url) {
			if (url == null || url.length() == 0) {
				if (state != State.LOADED)
					cancelLoad();
				
				this.state = State.LOADED;
				this.url = null;
				this.loadTask = null;
				view.setImageBitmap(null);
				return;
			}
			
			boolean urlEquals = this.url == null ? url == null : this.url.equals(url);
			if (!urlEquals && state == State.LOADING)
				cancelLoad();
			else if (!urlEquals && state == State.QUEUED)
				cancelLoad();
			else if (urlEquals && (state == State.LOADING || state == State.QUEUED))
				return;
			
			this.url = url;
			this.loadTask = new LoadImageTask(view);
			
			try {
				state = State.QUEUED;
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					loadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
				else
					loadTask.execute(url);
			}
			catch (RejectedExecutionException e) {
			}
		}
		
		private void cancelLoad() {
			if (loadTask != null)
				loadTask.cancel(true);
		}
		
		private class LoadImageTask extends AsyncTask<Object, Integer, Bitmap> {
			
			private static final int DEFAULT_BUFFER_SIZE = 10000;
			
			private ImageView view;
			
			public LoadImageTask(ImageView view) {
				this.view = view;
			}
			
			@Override
			protected void onPreExecute() {
				ConnectivityManager connectivityService = (ConnectivityManager)view.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = connectivityService.getActiveNetworkInfo();
			    if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
					this.cancel(false);
					ImageLoadState.this.state = State.DISCONNECTED;
				}
			}
			
			@Override
			protected Bitmap doInBackground(Object... params) {

				String url = (String)params[0];
				
				ImageLoadState.this.state = State.LOADING;
				
				Bitmap bitmap;
				try {
					byte[] imageData = loadImageData(url);
					if (imageData != null && imageData.length != 0)
						bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
					else 
						return null;
				}
				catch (Exception e) {
					Log.w("LoadImageTask", "Could not load image", e);
					return null;
				}
				
				return bitmap;
			}

			@Override
			protected void onPostExecute(Bitmap bitmap) {
				if (bitmap == null) {
					state = State.ERROR;
					view.setImageBitmap(null);
				}
				else {
					state = State.LOADED;
					view.setImageBitmap(bitmap);
				}
			}
			
			private byte[] loadImageData(String imageUrl) throws IOException {
		        URL url = new URL(imageUrl);
		        
		        if (isCancelled())
		        	return null;
		        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		        
		        if (isCancelled())
		        	return null;
		        
		        int fileSize = connection.getContentLength();
		        Log.d("LoadImageTask", "loading image " + imageUrl + " (" + (fileSize <= 0 ? "size unknown" : Integer.toString(fileSize)) + ")");
		        
		        BufferedInputStream is = new BufferedInputStream(connection.getInputStream(), fileSize <= 0 ? DEFAULT_BUFFER_SIZE : fileSize);
		        
		        try {   
		            if (fileSize <= 0) {
		                ByteArrayOutputStream buf = new ByteArrayOutputStream(DEFAULT_BUFFER_SIZE);
		                
		                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		                int bytesRead = 0;
		                while (bytesRead != -1) {
		                    bytesRead = is.read(buffer, 0, DEFAULT_BUFFER_SIZE);
		                    if (bytesRead > 0)
		                        buf.write(buffer, 0, bytesRead);
		                }
		                return buf.toByteArray();
		            } 
		            else {
		                byte[] imageData = new byte[fileSize];

		                int bytesRead = 0;
		                int offset = 0;
		                while (bytesRead != -1 && offset < fileSize) {
		                    bytesRead = is.read(imageData, offset, fileSize - offset);
		                    offset += bytesRead;
		                }
		                return imageData;
		            }
		        }
		        catch (Exception e) {
					Log.w("LoadImageTask", "Could not read image in buffer", e);
					return null;
				} 
		        finally {
		            try {
		                is.close();
		                connection.disconnect();
		            } 
		            catch (Exception e) {
		            	Log.d("LoadImageTask", "Could not close stream", e);
		            }
		        }
		    }
		}
	}
	
	
	public boolean loadImage(ImageView view, String url) {
		ImageLoadState loadState = (ImageLoadState)view.getTag(viewTag);		
		if (loadState == null) {
			loadState = new ImageLoadState();
			view.setTag(viewTag, loadState);
		}
		
		loadState.update(view, url);
		
		return false;
	}
	
}