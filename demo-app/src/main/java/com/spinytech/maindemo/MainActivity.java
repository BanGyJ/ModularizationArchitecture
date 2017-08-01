package com.spinytech.maindemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.spinytech.api.RouterApi;
import com.spinytech.macore.router.LocalRouter;
import com.spinytech.macore.router.RouterRequest;
import com.spinytech.macore.router.RouterResponse;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_local_sync_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RouterResponse response = LocalRouter.getInstance()
                            .route(MainActivity.this, RouterRequest.obtain(MainActivity.this)
                                    .path(RouterApi.Action.MAIN_SYNC)
                                    .data("1", "Hello")
                                    .data("2", "World"));
                    Toast.makeText(MainActivity.this, response.get(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.main_local_async_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final RouterResponse response = LocalRouter.getInstance()
                            .route(MainActivity.this, RouterRequest.obtain(MainActivity.this)
                                    .path(RouterApi.Action.MAIN_ASYNC)
                                    .data("1", "Hello")
                                    .data("2", "World"));
                    Toast.makeText(MainActivity.this, "please wait", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String result = response.get();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Toast.makeText(MainActivity.this, result,
                                                    Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        findViewById(R.id.main_play_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long startTime = System.currentTimeMillis();
               /* final RouterRequest request = new RouterRequest.Builder(getApplicationContext())
                        .domain("com.spinytech.maindemo:music")
                        .group("music")
                        .action("play")
                        .build();*/
                try {
                    final RouterResponse response = LocalRouter.getInstance()
                            .route(MainActivity.this, RouterRequest.obtain(MainActivity.this)
                                    .domain("com.spinytech.maindemo:music")
                                    .path(RouterApi.Action.MUSIC_PLAY));
                    response.isAsync();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String temp = response.getData();
                                final long time = System.currentTimeMillis() - startTime;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Toast.makeText(MainActivity.this,
                                                    "async:" + response.isAsync() + " cost:" + time
                                                            + " response:" + response.get(),
                                                    Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.main_stop_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long startTime = System.currentTimeMillis();
                try {
                    final RouterResponse response = LocalRouter.getInstance()
                            .route(MainActivity.this, RouterRequest.obtain(MainActivity.this)
                                    .domain("com.spinytech.maindemo:music")
                                    .path(RouterApi.Action.MUSIC_STOP));
                    response.isAsync();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String temp = response.getData();
                                final long time = System.currentTimeMillis() - startTime;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Toast.makeText(MainActivity.this,
                                                    "async:" + response.isAsync() + " cost:" + time
                                                            + " response:" + response.get(),
                                                    Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        findViewById(R.id.main_music_shutdown_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long startTime = System.currentTimeMillis();
                try {
                    final RouterResponse response = LocalRouter.getInstance()
                            .route(MainActivity.this, RouterRequest.obtain(MainActivity.this)
                                    .domain("com.spinytech.maindemo:music")
                                    .path(RouterApi.Action.MUSIC_SHUTDOWN));
                    response.isAsync();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String temp = response.getData();
                                final long time = System.currentTimeMillis() - startTime;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Toast.makeText(MainActivity.this,
                                                    "async:" + response.isAsync() + " cost:" + time
                                                            + " response:" + response.get(),
                                                    Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.main_wide_shutdown_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalRouter.getInstance().stopWideRouter();
            }
        });
        findViewById(R.id.main_pic_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long startTime = System.currentTimeMillis();
                try {
                    final RouterResponse response = LocalRouter.getInstance()
                            .route(MainActivity.this, RouterRequest.obtain(MainActivity.this)
                                    .domain("com.spinytech.maindemo:pic")
                                    .path(RouterApi.Action.PIC_PIC)
                                    .data("is_big", "0"));
                    response.isAsync();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String temp = response.getData();
                                final long time = System.currentTimeMillis() - startTime;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Toast.makeText(MainActivity.this,
                                                    "async:" + response.isAsync() + " cost:" + time
                                                            + " response:" + response.get(),
                                                    Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.main_big_pic_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long startTime = System.currentTimeMillis();
                try {
                    final RouterResponse response = LocalRouter.getInstance()
                            .route(MainActivity.this, RouterRequest.obtain(MainActivity.this)
                                    .domain("com.spinytech.maindemo:pic")
                                    .path(RouterApi.Action.PIC_PIC)
                                    .data("is_big", "1"));
                    response.isAsync();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String temp = response.getData();
                                final long time = System.currentTimeMillis() - startTime;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Toast.makeText(MainActivity.this,
                                                    "async:" + response.isAsync() + " cost:" + time
                                                            + " response:" + response.get(),
                                                    Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.main_web_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LocalRouter.getInstance()
                            .route(MainActivity.this, RouterRequest.obtain(MainActivity.this)
                                    .path(RouterApi.Action.WEB_WEB)
                            );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.main_attach_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RouterResponse response = LocalRouter.getInstance()
                            .route(MainActivity.this, RouterRequest.obtain(MainActivity.this)
                                    .path(RouterApi.Action.MAIN_ATTACHMENT)
                                    .object(findViewById(R.id.main_attach_btn))
                            );
                    if (response.getObject() instanceof Toast) {
                        ((Toast) response.getObject()).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
